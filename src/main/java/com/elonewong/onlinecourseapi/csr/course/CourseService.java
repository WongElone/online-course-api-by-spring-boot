package com.elonewong.onlinecourseapi.csr.course;

import com.elonewong.onlinecourseapi.csr.category.CategoryService;
import com.elonewong.onlinecourseapi.csr.chapter.Chapter;
import com.elonewong.onlinecourseapi.csr.chapter.ChapterOrder;
import com.elonewong.onlinecourseapi.csr.chapter.ChapterRepository;
import com.elonewong.onlinecourseapi.csr.lesson.LessonOrder;
import com.elonewong.onlinecourseapi.csr.student.StudentResponse;
import com.elonewong.onlinecourseapi.csr.student.StudentResponseMapper;
import com.elonewong.onlinecourseapi.csr.teacher.TeacherService;
import com.elonewong.onlinecourseapi.csr.user.Profile;
import com.elonewong.onlinecourseapi.csr.user.Role;
import com.elonewong.onlinecourseapi.exception.AuthorizationException;
import com.elonewong.onlinecourseapi.exception.BadRequestException;
import com.elonewong.onlinecourseapi.exception.ResourceNotFoundException;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private CourseRepository courseRepository;
    private TeacherService teacherService;
    private CourseResponseMapper courseResponseMapper;
    private StudentResponseMapper studentResponseMapper;
    private CategoryService categoryService;
    private ChapterRepository chapterRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository, TeacherService teacherService, CourseResponseMapper courseResponseMapper, StudentResponseMapper studentResponseMapper, CategoryService categoryService, ChapterRepository chapterRepository) {
        this.courseRepository = courseRepository;
        this.teacherService = teacherService;
        this.courseResponseMapper = courseResponseMapper;
        this.studentResponseMapper = studentResponseMapper;
        this.categoryService = categoryService;
        this.chapterRepository = chapterRepository;
    }

    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream().map(courseResponseMapper).collect(Collectors.toList());
    }

    public CourseResponse getOneCourse(String courseId) {
        Course course = findCourseById(courseId);
        return courseResponseMapper.genResponse(
                course,
                courseRepository.findStudentsCountOfTheCourse(courseId) // don't use course.students.size() because this can lead to huge amount of records in the query set
        );
    }

    public List<StudentResponse> getStudentsOfTheCourse(String courseId) {
        Course course = findCourseById(courseId);
        return course.getStudents().stream().map(studentResponseMapper).collect(Collectors.toList());
    }

    public CourseResponse addOneCourse(CourseRequest courseRequest, String teacherId) {
        Course course = Course.builder()
                .title(courseRequest.title())
                .categories(categoryService.findCategoriesByIds(courseRequest.categoryIds()))
                .teachers(List.of(teacherService.findTeacherById(teacherId)))
                .students(List.of())
                .build();
        return courseResponseMapper.apply(courseRepository.save(course));
    }

    public CourseResponse updateOneCourse(String courseId, CourseRequest courseRequest) {
        Course course = findCourseById(courseId);
        course.setTitle(courseRequest.title());
        course.setCategories(categoryService.findCategoriesByIds(courseRequest.categoryIds()));
        return courseResponseMapper.apply(courseRepository.save(course));
    }

    @Transactional
    public void updateChapterOrders(String courseId, List<ChapterOrder> newChapterOrders, Profile userProfile) throws AuthorizationException {
        Course course = this.findCourseById(courseId);
        this.validateIsTeacherOfTheCourse(userProfile, course);
        List<Chapter> chapters = course.getChapters();
        
        this.validateChapterOrdersData(newChapterOrders, chapters);

        StringBuilder jpqlForUpdateChapterOrder_p1 = new StringBuilder("UPDATE Chapter c SET c.orderInCourse = CASE c.id ");
        StringBuilder jpqlForUpdateChapterOrder_p2 = new StringBuilder();

        for (int i = 0; i < newChapterOrders.size(); i++) {
            jpqlForUpdateChapterOrder_p1.append("WHEN :chapterId_" + i + " THEN :chapterOrderNum_" + i + " ");
            jpqlForUpdateChapterOrder_p2.append(":chapterId_" + i).append(
                    (i == newChapterOrders.size() - 1) ? ")" : ", "
            );
        }
        jpqlForUpdateChapterOrder_p1.append(" ELSE c.orderInCourse END WHERE c.id in (");

        Query query = chapterRepository.getEntityManager().createQuery(
                jpqlForUpdateChapterOrder_p1 + jpqlForUpdateChapterOrder_p2.toString()
        );
        for (int i = 0; i < newChapterOrders.size(); i++) {
            ChapterOrder order = newChapterOrders.get(i);
            query.setParameter("chapterId_" + i, order.chapterId())
                    .setParameter("chapterOrderNum_" + i, order.orderNum());
        }
        query.executeUpdate();
    }

    private void validateChapterOrdersData(List<ChapterOrder> newChapterOrders, List<Chapter> chapters) {
        // check correct number of lessons
        if (newChapterOrders.size() != chapters.size()) {
            throw new BadRequestException("Size of order list does not match number of chapters in this lesson");
        }
        List<String> mentionedChaptersIds = new ArrayList<>();
        List<Integer> mentionedOrderNum = new ArrayList<>();
        for (ChapterOrder order : newChapterOrders) {
            // check chapter order max number matches number of chapters, min number is 1
            if (order.orderNum() < 1 || order.orderNum() > chapters.size()) {
                throw new BadRequestException("Some chapter order number is out of range of number of chapters");
            }
            // check chapter id are all distinct
            if (mentionedChaptersIds.contains(order.chapterId())) {
                throw new BadRequestException("Duplication of chapter ids found in the order list");
            }
            // check order number are all distinct
            if (mentionedOrderNum.contains(order.orderNum())) {
                throw new BadRequestException("Duplication of order number found in the order list");
            }
            mentionedChaptersIds.add(order.chapterId());
            mentionedOrderNum.add(order.orderNum());
        }
        if (chapterRepository.countByIds(mentionedChaptersIds) != chapters.size()) {
            throw new BadRequestException("Some provided chapter ids do not exist");
        }
    }

    public boolean isTeacherOfTheCourse(String teacherId, Course course) {
        return course.getTeachers().stream().anyMatch(
                courseTeacher -> teacherId.equals(courseTeacher.getId())
        );
    }

    public boolean isTeacherOfTheCourse(String teacherId, String courseId) {
        Course course = findCourseById(courseId);
        return isTeacherOfTheCourse(teacherId, course);
    }

    public boolean isTeacherOfTheCourse(Profile userProfile, Course course) {
        return Role.TEACHER.equals(userProfile.role())
                && this.isTeacherOfTheCourse(userProfile.profileTableId(), course);
    }

    public void validateIsTeacherOfTheCourse(Profile userProfile, Course course) throws AuthorizationException {
        if (!this.isTeacherOfTheCourse(userProfile, course)) {
            throw new AuthorizationException("You are not one of the teachers of the course");
        }
    }

    public void validateIsTeacherOfTheCourse(Profile userProfile, String courseId) throws AuthorizationException {
        Course course = this.findCourseById(courseId);
        this.validateIsTeacherOfTheCourse(userProfile, course);
    }

    public boolean isStudentOfTheCourse(String studentId, Course course) {
        return course.getStudents().stream().anyMatch(
                courseStudent -> studentId.equals(courseStudent.getId())
        );
    }

    public boolean isStudentOfTheCourse(Profile userProfile, Course course) {
        return Role.STUDENT.equals(userProfile.role())
                && this.isStudentOfTheCourse(userProfile.profileTableId(), course);
    }

    public Course findCourseById(String courseId) {
        return courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course id not found"));
    }

    public void validateHavingAccessToCourse(Profile userProfile, Course course) throws AuthorizationException {
        if (!this.isStudentOfTheCourse(userProfile, course) && !this.isTeacherOfTheCourse(userProfile, course)) {
            throw new AuthorizationException("You have no access to the course");
        }
    }

//    public List<Course> findCoursesByIds(List<String> courseIds) {
//        List<Course> courses = courseRepository.findAllCoursesByIds(courseIds);
//        if (courses.size() == courseIds.size()) {
//            return courses;
//        } else throw new ResourceNotFoundException("Some Ids not found");
//    }

}
