package com.elonewong.onlinecourseapi.course;

import com.elonewong.onlinecourseapi.csr.course.Course;
import com.elonewong.onlinecourseapi.csr.course.CourseRepository;
import com.elonewong.onlinecourseapi.csr.student.Student;
import com.elonewong.onlinecourseapi.csr.student.StudentRepository;
import com.elonewong.onlinecourseapi.csr.user.Role;
import com.elonewong.onlinecourseapi.csr.user.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase
public class CourseRepositoryTests {

    @Autowired
    private CourseRepository courseRepository;

    @Test
    public void test_CourseRepository_FindAllCoursesByIds_ReturnListOfCorrectSizeAndElements() {

        // Arrange
        List<Course> newCourses = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            newCourses.add(Course.builder()
                    .title("Course " + i)
                    .categories(List.of())
                    .teachers(List.of())
                    .students(List.of())
                    .build());
        }
        courseRepository.saveAll(newCourses);

        // Act
        List<Course> coursesFoundByIds = courseRepository.findAllCoursesByIds(
                List.of(newCourses.get(0).getId(), newCourses.get(1).getId())
        );

        // Assert
        Assertions.assertThat(coursesFoundByIds.size() ).isEqualTo(2);
        Assertions.assertThat(coursesFoundByIds).containsAll(
                List.of(newCourses.get(0), newCourses.get(1))
        );

    }

    @Test
    public void test_CourseRepository_FindStudentCountOfTheCourse_ReturnCorrectNumber() {

        // Arrange
        List<Student> newStudents = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            User newUser = User.builder()
                    .email("johndoe0" + i + "@example.com")
                    .firstName("John")
                    .lastName("Doe")
                    .role(Role.STUDENT)
                    .password("$2a$10$q.A37vTrXLxUgfZ7Tte.KuVCMgv0PlfZN11GPPaLlPQPExpgVP8Hy")
                    .build();
            newStudents.add(Student.builder()
                    .courses(List.of())
                    .user(newUser)
                    .build()
            );
        }
        Course newCourse = Course.builder()
                .title("Course 1")
                .categories(List.of())
                .teachers(List.of())
                .students(newStudents)
                .build();
        courseRepository.save(newCourse);

        // Act
        Integer foundStudentCount = courseRepository.findStudentsCountOfTheCourse(newCourse.getId());

        // Assert
        Assertions.assertThat(foundStudentCount).isEqualTo(2);

    }
}
