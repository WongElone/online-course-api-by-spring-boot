package com.elonewong.onlinecourseapi.course;

import com.elonewong.onlinecourseapi.csr.category.*;
import com.elonewong.onlinecourseapi.csr.course.*;
import com.elonewong.onlinecourseapi.csr.student.Student;
import com.elonewong.onlinecourseapi.csr.student.StudentResponse;
import com.elonewong.onlinecourseapi.csr.student.StudentResponseMapper;
import com.elonewong.onlinecourseapi.csr.teacher.Teacher;
import com.elonewong.onlinecourseapi.csr.teacher.TeacherService;
import com.elonewong.onlinecourseapi.csr.user.Role;
import com.elonewong.onlinecourseapi.csr.user.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTests {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private CategoryService categoryService;
    @Mock
    private TeacherService teacherService;
    @Spy
    private CourseResponseMapper courseResponseMapper;
    @Spy
    private StudentResponseMapper studentResponseMapper;
    @InjectMocks
    private CourseService courseService;

    private Category category1;
    private Course course1;
    private Teacher teacher1;
    private Student student1;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this); // to inject the @InjectMocks fields

        course1 = Course.builder()
                .id("course-1")
                .title("Course 1")
                .students(List.of())
                .build();
        category1 = Category.builder()
                .id("category-1")
                .name("Category 1")
                .courses(List.of(course1))
                .build();
        User user1 = User.builder()
                .id("user-01")
                .role(Role.TEACHER)
                .email("john.doe01@example.com")
                .password("password")
                .build();
        teacher1 = Teacher.builder()
                .id("teacher-01")
                .user(user1)
                .courses(List.of(course1))
                .build();
        User user2 = User.builder()
                .id("user-02")
                .role(Role.STUDENT)
                .email("john.doe02@example.com")
                .password("password")
                .build();
        student1 = Student.builder()
                .id("student-01")
                .user(user2)
                .courses(List.of(course1))
                .build();
        course1.setTeachers(List.of(teacher1));
        course1.setCategories(List.of(category1));
    }

    @Test
    public void test_CourseService_GetAllCourses_ReturnsListOfCorrectSizeAndElements() {
        when(courseRepository.findAll()).thenReturn(List.of(course1));

        List<CourseResponse> coursesResponses = courseService.getAllCourses();

        Assertions.assertThat(coursesResponses).hasSize(1);
        Assertions.assertThat(coursesResponses).containsAll(List.of(
                courseResponseMapper.apply(course1)
        ));
    }

    @Test
    public void test_CourseService_GetOneCourse_ReturnsCorrectCourse() {
        when(courseRepository.findById(course1.getId())).thenReturn(Optional.ofNullable(course1));
        when(courseRepository.findStudentsCountOfTheCourse(course1.getId())).thenReturn(course1.getStudents().size());

        CourseResponse courseResponse = courseService.getOneCourse(course1.getId());

        Assertions.assertThat(courseResponse).isEqualTo(
                courseResponseMapper.genResponse(course1, course1.getStudents().size())
        );
    }

    @Test
    public void test_CourseService_getStudentsOfTheCourse_ReturnsListOfCorrectSizeAndElements() {
        course1.setStudents(List.of(student1));
        when(courseRepository.findById(course1.getId())).thenReturn(Optional.ofNullable(course1));

        List<StudentResponse> studentsOfTheCourseResponse = courseService.getStudentsOfTheCourse(course1.getId());

        Assertions.assertThat(studentsOfTheCourseResponse).hasSize(1);
        Assertions.assertThat(studentsOfTheCourseResponse).containsAll(List.of(
                studentResponseMapper.apply(student1)
        ));
    }

    @Test
    public void test_CourseService_AddOneCourse_ReturnsAddedCourseResponse() {
        List<String> course1CategoryIds = course1.getCategories().stream().map(Category::getId).collect(Collectors.toList());

        // TODO: check arguments of courseRepository.save(), i.e. course instance, in the test, check if match course1 request object (i.e. id should be null)
        when(courseRepository.save(any(Course.class))).thenReturn(course1);
        when(teacherService.findTeacherById(teacher1.getId())).thenReturn(teacher1);
        when(categoryService.findCategoriesByIds(course1CategoryIds)).thenReturn(List.of(category1));

        CourseResponse courseResponse = courseService.addOneCourse(new CourseRequest(
                course1.getTitle(), course1CategoryIds
        ), teacher1.getId());

        Assertions.assertThat(courseResponse).isEqualTo(
            courseResponseMapper.apply(course1)
        );
    }

    @Test
    public void test_CourseService_UpdateOneCourse_ReturnsUpdatedCourse() {
        String newTitle = "Course 1 Updated";
        List<String> course1CategoryIds = course1.getCategories().stream().map(Category::getId).collect(Collectors.toList());
        Course updatedCourse = Course.builder()
            .id(course1.getId())
            .title(newTitle)
            .categories(course1.getCategories())
            .teachers(course1.getTeachers())
            .students(course1.getStudents())
            .build();
        when(courseRepository.findById(course1.getId())).thenReturn(Optional.ofNullable(course1));
        when(courseRepository.save(course1)).thenReturn(updatedCourse);

        CourseResponse courseResponse = courseService.updateOneCourse(course1.getId(), new CourseRequest(newTitle, course1CategoryIds));

        Assertions.assertThat(courseResponse).isEqualTo(
                courseResponseMapper.apply(updatedCourse)
        );
    }

    @Test
    public void test_CourseService_IsTeacherOfTheCourse_whenProvideTeacherIdAndCourseIdOfACourse_ReturnsTrue() {
        when(courseRepository.findById(course1.getId())).thenReturn(Optional.ofNullable(course1));

        Teacher firstTeacher = course1.getTeachers().stream().findFirst().orElseThrow(RuntimeException::new);
        boolean isTeacherOfTheCourse = courseService.isTeacherOfTheCourse(firstTeacher.getId(), course1.getId());

        Assertions.assertThat(isTeacherOfTheCourse).isTrue();
    }

    @Test
    public void test_CourseService_FindCourseById_ReturnCorrectCourse() {
        when(courseRepository.findById(course1.getId())).thenReturn(Optional.ofNullable(course1));

        Course course = courseService.findCourseById(course1.getId());

        Assertions.assertThat(course).isEqualTo(course1);
    }

}