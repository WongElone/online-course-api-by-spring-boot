package com.elonewong.onlinecourseapi.csr.chapter;

import com.elonewong.onlinecourseapi.csr.course.Course;
import com.elonewong.onlinecourseapi.csr.course.CourseService;
import com.elonewong.onlinecourseapi.csr.lesson.Lesson;
import com.elonewong.onlinecourseapi.csr.lesson.LessonOrder;
import com.elonewong.onlinecourseapi.csr.lesson.LessonRepository;
import com.elonewong.onlinecourseapi.csr.lesson.LessonService;
import com.elonewong.onlinecourseapi.csr.user.Profile;
import com.elonewong.onlinecourseapi.exception.AuthorizationException;
import com.elonewong.onlinecourseapi.exception.BadRequestException;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChapterService {

    private CourseService courseService;
    private ChapterResponseMapper chapterResponseMapper;
    private ChapterRepository chapterRepository;
    private LessonService lessonService;
    private LessonRepository lessonRepository;

    @Autowired
    public ChapterService(CourseService courseService, ChapterResponseMapper chapterResponseMapper, ChapterRepository chapterRepository, @Lazy LessonService lessonService, LessonRepository lessonRepository) {
        this.courseService = courseService;
        this.chapterResponseMapper = chapterResponseMapper;
        this.chapterRepository = chapterRepository;
        this.lessonService = lessonService;
        this.lessonRepository = lessonRepository;
    }

    public ChapterResponse addOneChapterForOneCourse(String courseId, ChapterRequest chapterRequest, Profile userProfile) throws AuthorizationException {
        Course course = courseService.findCourseById(courseId);
        courseService.validateIsTeacherOfTheCourse(userProfile, course);
        Chapter newChapter = Chapter.builder()
                .title(chapterRequest.title())
                .course(course)
                .orderInCourse(course.getChapters().size() + 1)
                .lessons(List.of())
                .build();
        return chapterResponseMapper.apply(chapterRepository.save(newChapter));
    }

    public List<ChapterResponse> getAllChaptersOfOneCourse(String courseId, Profile userProfile) throws AuthorizationException {
        Course course = courseService.findCourseById(courseId);
        courseService.validateHavingAccessToCourse(userProfile, course);
        return course.getChapters().stream().map(chapterResponseMapper).collect(Collectors.toList());
    }

    public ChapterResponse updateOneChapterOfOneCourse(String courseId, String chapterId, ChapterRequest chapterRequest, Profile userProfile) throws AuthorizationException {
        Chapter chapter = this.findOneChapterOfACourse(courseId, chapterId);
        courseService.validateIsTeacherOfTheCourse(userProfile, chapter.getCourse());

        chapter.setTitle(chapterRequest.title());

        return chapterResponseMapper.apply(chapterRepository.save(chapter));
    }

    public void deleteOneChapter(String courseId, String chapterId, Profile userProfile) throws AuthorizationException {
        Chapter chapter = this.findOneChapterOfACourse(courseId, chapterId);
        courseService.validateIsTeacherOfTheCourse(userProfile, chapter.getCourse());
        chapterRepository.delete(chapter);
    }

    private Chapter findOneChapterById(String chapterId) {
        return chapterRepository.findById(chapterId)
                .orElseThrow(() -> new BadRequestException("chapter id not found"));
    }

    public Chapter findOneChapterOfACourse(String courseId, String chapterId) {
        Chapter chapter = this.findOneChapterById(chapterId);
        if (!courseId.equals(chapter.getCourse().getId())) {
            throw new BadRequestException("course id not exists or the chapter does not belong to the course");
        }
        return chapter;
    }

    @Transactional
    public void updateLessonsOrder(String courseId, String chapterId, List<LessonOrder> newLessonOrders, Profile userProfile) throws AuthorizationException {
        Chapter chapter = this.findOneChapterOfACourse(courseId, chapterId);
        courseService.validateIsTeacherOfTheCourse(userProfile, chapter.getCourse());
        List<Lesson> lessons = chapter.getLessons();

        this.checkLessonOrderDataIsValid(newLessonOrders, lessons);

        // update each lesson order
        StringBuilder jpqlForUpdateLessonOrder_p1 = new StringBuilder("UPDATE Lesson l SET l.orderInChapter = CASE l.id ");
        StringBuilder jpqlForUpdateLessonOrder_p2 = new StringBuilder();

        for (int i = 0; i < newLessonOrders.size(); i++) {
            jpqlForUpdateLessonOrder_p1.append("WHEN :lessonId_" + i + " THEN :lessonOrderNum_" + i + " ");
            jpqlForUpdateLessonOrder_p2.append(":lessonId_" + i).append(
                (i == newLessonOrders.size() - 1) ? ")" : ", "
            );
        }
        jpqlForUpdateLessonOrder_p1.append(" ELSE l.orderInChapter END WHERE l.id in (");

        Query query = lessonRepository.getEntityManager().createQuery(
                jpqlForUpdateLessonOrder_p1 + jpqlForUpdateLessonOrder_p2.toString()
        );
        for (int i = 0; i < newLessonOrders.size(); i++) {
            LessonOrder order = newLessonOrders.get(i);
            query.setParameter("lessonId_" + i, order.lessonId())
                    .setParameter("lessonOrderNum_" + i, order.orderNum());
        }
        query.executeUpdate();
    }

    private void checkLessonOrderDataIsValid(List<LessonOrder> newLessonOrders, List<Lesson> lessons) {
        // check correct number of lessons
        if (newLessonOrders.size() != lessons.size()) {
            throw new BadRequestException("Size of order list does not match number of lessons in this chapter");
        }
        List<String> mentionedLessonIds = new ArrayList<>();
        List<Integer> mentionedOrderNum = new ArrayList<>();
        for (LessonOrder newLessonOrder : newLessonOrders) {
            // check lesson order max number matches number of lessons, min number is 1
            if (newLessonOrder.orderNum() < 1 || newLessonOrder.orderNum() > lessons.size()) {
                throw new BadRequestException("Some lesson order number is out of range of number of lessons");
            }
            // check lesson id are all distinct
            if (mentionedLessonIds.contains(newLessonOrder.lessonId())) {
                throw new BadRequestException("Duplication of lesson ids found in the order list");
            }
            // check order number are all distinct
            if (mentionedOrderNum.contains(newLessonOrder.orderNum())) {
                throw new BadRequestException("Duplication of order number found in the order list");
            }
            mentionedLessonIds.add(newLessonOrder.lessonId());
            mentionedOrderNum.add(newLessonOrder.orderNum());
        }
        if (lessonRepository.countByIds(mentionedLessonIds) != lessons.size()) {
            throw new BadRequestException("Some provided lesson ids do not exist");
        }
    }

}
