package com.elonewong.onlinecourseapi.enrollment;

import com.elonewong.onlinecourseapi.csr.course.Course;
import com.elonewong.onlinecourseapi.csr.course.CourseRepository;
import com.elonewong.onlinecourseapi.csr.enrollment.Enrollment;
import com.elonewong.onlinecourseapi.csr.enrollment.EnrollmentRepository;
import com.elonewong.onlinecourseapi.csr.student.Student;
import com.elonewong.onlinecourseapi.csr.student.StudentRepository;
import com.elonewong.onlinecourseapi.csr.teacher.Teacher;
import com.elonewong.onlinecourseapi.csr.teacher.TeacherRepository;
import com.elonewong.onlinecourseapi.csr.user.Role;
import com.elonewong.onlinecourseapi.csr.user.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class EnrollmentRepositoryTests {

    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private CourseRepository courseRepository;

    @Test
    public void test_EnrollmentRepository_FindActiveEnrollmentByGivingCourseIdAndStudentId_ReturnActiveEnrollment() {

        // Arrange
        User user1 = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("johndoe01@example.com")
                .role(Role.STUDENT)
                .password("password")
                .build();
        Student newStudent = Student.builder()
                .user(user1)
                .courses(List.of())
                .profilePicture("path/to/profile/picture")
                .build();
        studentRepository.save(newStudent);

        User user2 = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("johndoe02@example.com")
                .role(Role.STUDENT)
                .password("password")
                .build();
        Teacher newTeacher = Teacher.builder()
                .user(user2)
                .courses(List.of())
                .profilePicture("path/to/profile/picture")
                .build();
        teacherRepository.save(newTeacher);

        List<Course> courses = new ArrayList<>();
        List<Enrollment> enrollments = new ArrayList<>();
        for (int i = 0; i < 2; i ++){
            Course newCourse = Course.builder()
                    .title("Course 0" + i)
                    .categories(new ArrayList<>())
                    .teachers(List.of(newTeacher))
                    .students(List.of(newStudent))
                    .build();
            Enrollment enrollment = Enrollment.builder()
                    .student(newStudent)
                    .course(newCourse)
                    .build();
            courses.add(newCourse);
            enrollments.add(enrollment);
        }
        courseRepository.saveAll(courses);
        enrollmentRepository.saveAll(enrollments);

        // Act
        Optional<Enrollment> activeEnrollmentFound = enrollmentRepository.findActiveEnrollment(courses.get(0).getId(), newStudent.getId());

        // Assert
        Assertions.assertThat(activeEnrollmentFound).isPresent();
        Assertions.assertThat(activeEnrollmentFound.get()).isEqualTo(enrollments.get(0));

    }

    @Test
    public void test_EnrollmentRepository_FindEnrollmentByStudentId_ReturnListOfCorrectSizeAndElements() {

        // Arrange
        User user1 = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("johndoe01@example.com")
                .role(Role.STUDENT)
                .password("password")
                .build();
        Student newStudent = Student.builder()
                .user(user1)
                .courses(List.of())
                .profilePicture("path/to/profile/picture")
                .build();
        studentRepository.save(newStudent);

        User user2 = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("johndoe02@example.com")
                .role(Role.STUDENT)
                .password("password")
                .build();
        Teacher newTeacher = Teacher.builder()
                .user(user2)
                .courses(List.of())
                .profilePicture("path/to/profile/picture")
                .build();
        teacherRepository.save(newTeacher);

        List<Course> courses = new ArrayList<>();
        List<Enrollment> enrollments = new ArrayList<>();
        for (int i = 0; i < 2; i ++){
            Course newCourse = Course.builder()
                    .title("Course 0" + i)
                    .categories(new ArrayList<>())
                    .teachers(List.of(newTeacher))
                    .students(List.of(newStudent))
                    .build();
            Enrollment enrollment = Enrollment.builder()
                    .student(newStudent)
                    .course(newCourse)
                    .build();
            courses.add(newCourse);
            enrollments.add(enrollment);
        }
        courseRepository.saveAll(courses);
        enrollmentRepository.saveAll(enrollments);

        // Act
        List<Enrollment> enrollmentsFoundByStudentId = enrollmentRepository.findEnrollmentsByStudentId(newStudent.getId());

        // Assert
        Assertions.assertThat(enrollmentsFoundByStudentId).hasSize(2);
        Assertions.assertThat(enrollmentsFoundByStudentId).containsAll(
                List.of(enrollments.get(0), enrollments.get(1))
        );
    }

}
