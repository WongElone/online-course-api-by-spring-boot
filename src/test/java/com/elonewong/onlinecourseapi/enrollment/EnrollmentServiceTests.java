package com.elonewong.onlinecourseapi.enrollment;

import com.elonewong.onlinecourseapi.common.PaymentType;
import com.elonewong.onlinecourseapi.csr.course.Course;
import com.elonewong.onlinecourseapi.csr.course.CourseRepository;
import com.elonewong.onlinecourseapi.csr.course.CourseService;
import com.elonewong.onlinecourseapi.csr.enrollment.*;
import com.elonewong.onlinecourseapi.csr.student.Student;
import com.elonewong.onlinecourseapi.csr.student.StudentRepository;
import com.elonewong.onlinecourseapi.csr.student.StudentService;
import com.elonewong.onlinecourseapi.csr.teacher.Teacher;
import com.elonewong.onlinecourseapi.csr.user.Role;
import com.elonewong.onlinecourseapi.csr.user.User;
import com.elonewong.onlinecourseapi.exception.BadRequestException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EnrollmentServiceTests {

    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private CourseService courseService;
    @Mock
    private StudentService studentService;
    @Spy
    private EnrollmentResponseMapper enrollmentResponseMapper;
    @InjectMocks
    private EnrollmentService enrollmentService;

    private Course course1;
    private Course course2;
    private Teacher teacher1;
    private Student student1;
    private Student student2;
    private Enrollment enrollment1;
    private Enrollment enrollment2;
    private Enrollment enrollment3;
    private Enrollment enrollment4;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        course1 = Course.builder()
                .id("course-1")
                .title("Course 1")
                .students(List.of())
                .build();
        course2 = Course.builder()
                .id("course-2")
                .title("Course 2")
                .students(List.of())
                .build();
        teacher1 = Teacher.builder()
                .id("teacher-01")
                .user(User.builder()
                        .id("user-01")
                        .role(Role.TEACHER)
                        .email("john.doe01@example.com")
                        .password("password")
                        .build())
                .courses(List.of(course1))
                .build();
        course1.setTeachers(List.of(teacher1));
        course2.setTeachers(List.of(teacher1));
        final List<Course> coursesOfStudent1 = new ArrayList<>();
        coursesOfStudent1.add(course1);
        coursesOfStudent1.add(course2);
        student1 = Student.builder()
                .id("student-01")
                .user(User.builder()
                        .id("user-02")
                        .role(Role.STUDENT)
                        .email("john.doe02@example.com")
                        .password("password")
                        .build())
                .courses(coursesOfStudent1)
                .build();
        student2 = Student.builder()
                .id("student-02")
                .user(User.builder()
                        .id("user-03")
                        .role(Role.STUDENT)
                        .email("john.doe03@example.com")
                        .password("password")
                        .build())
                .courses(List.of(course1))
                .build();


        enrollment1 = Enrollment.builder()
                .id("enrollment-1")
                .course(course1)
                .student(student1)
                .enrollAt(Instant.ofEpochMilli(System.currentTimeMillis() - 18800))
                .payment(EnrollmentPayment.builder()
                        .amount(399)
                        .type(PaymentType.BANK_TRANSFER)
                        .description("Enrollment 1 Description")
                        .build()
                )
                .build();
        enrollment2 = Enrollment.builder()
                .id("enrollment-2")
                .course(course2)
                .student(student1)
                .enrollAt(Instant.ofEpochMilli(System.currentTimeMillis() - 18800))
                .dropAt(Instant.now())
                .payment(EnrollmentPayment.builder()
                        .amount(399)
                        .type(PaymentType.BANK_TRANSFER)
                        .description("Enrollment 2 Description")
                        .build()
                )
                .build();
        enrollment3 = Enrollment.builder()
                .id("enrollment-3")
                .course(course1)
                .student(student2)
                .enrollAt(Instant.ofEpochMilli(System.currentTimeMillis() - 18800))
                .dropAt(Instant.now())
                .payment(EnrollmentPayment.builder()
                        .amount(399)
                        .type(PaymentType.BANK_TRANSFER)
                        .description("Enrollment 3 Description")
                        .build()
                )
                .build();
        enrollment4 = Enrollment.builder()
                .id("enrollment-4")
                .course(course2)
                .student(student1)
                .enrollAt(Instant.now())
                .payment(EnrollmentPayment.builder()
                        .amount(399)
                        .type(PaymentType.BANK_TRANSFER)
                        .description("Enrollment 4 Description")
                        .build()
                )
                .build();
    }

    @Test
    public void test_EnrollmentService_GetEnrollmentsByStudentId_ReturnsListOfCorrectSizeAndElements() {
        when(enrollmentRepository.findEnrollmentsByStudentId(student1.getId())).thenReturn(List.of(enrollment1, enrollment2));

        final List<EnrollmentRespond> enrollmentResponses = enrollmentService.getEnrollmentsByStudentId(student1.getId());

        Assertions.assertThat(enrollmentResponses).hasSize(2);
        Assertions.assertThat(enrollmentResponses).containsAll(Stream.of(enrollment1, enrollment2)
                .map(enrollmentResponseMapper).collect(Collectors.toList()));
    }

    @Test
    public void test_EnrollmentService_EnrollOneCourse_WhenHasActiveEnrollmentThenThrowError() {
        when(courseService.findCourseById(course1.getId())).thenReturn(course1);
        when(studentService.findStudentById(student1.getId())).thenReturn(student1);
        when(enrollmentRepository.findActiveEnrollment(course1.getId(), student1.getId())).thenReturn(Optional.ofNullable(enrollment1));

        boolean failToEnroll = false;
        try {
            enrollmentService.enrollOneCourse(new EnrollCourseRequest(
                    course1.getId(),
                    new EnrollCourseRequest.EnrollmentPaymentRequest(
                            enrollment1.getPayment().getAmount(),
                            enrollment1.getPayment().getType()
                    )
            ), student1.getId());
        } catch (BadRequestException e) {
            failToEnroll = true;
        }

        Assertions.assertThat(failToEnroll).isTrue();
    }

    @Test
    public void test_EnrollmentService_EnrollOneCourse_WhenNoActiveEnrollmentThenReturnsCorrectEnrollmentResponse() {
        when(courseService.findCourseById(course2.getId())).thenReturn(course2);
        when(studentService.findStudentById(student1.getId())).thenReturn(student1);
        when(enrollmentRepository.findActiveEnrollment(course2.getId(), student1.getId())).thenReturn(Optional.empty());
        // TODO: check enrollment instance being saved has fields same as enrollment1 or not
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment4);

        EnrollmentRespond enrollmentRespond = null;
        boolean failToEnroll = false;
        try {
            enrollmentRespond = enrollmentService.enrollOneCourse(new EnrollCourseRequest(
                    course2.getId(),
                    new EnrollCourseRequest.EnrollmentPaymentRequest(
                            enrollment4.getPayment().getAmount(),
                            enrollment4.getPayment().getType()
                    )
            ), student1.getId());
        } catch (BadRequestException e) {
            failToEnroll = true;
        }

        Assertions.assertThat(failToEnroll).isFalse();
        Assertions.assertThat(enrollmentRespond).isNotNull();
        Assertions.assertThat(enrollmentRespond).isEqualTo(
                enrollmentResponseMapper.apply(enrollment4)
        );
    }

    @Test
    public void test_EnrollmentService_DropOneCourse_WhenNoActiveEnrollmentThenFailToDrop() {
        when(studentService.findStudentById(student1.getId())).thenReturn(student1);
        when(courseRepository.existsById(course2.getId())).thenReturn(true);
        when(enrollmentRepository.findActiveEnrollment(course2.getId(), student1.getId())).thenReturn(Optional.empty());

        boolean failToDrop = false;
        try {
            enrollmentService.dropOneCourse(course2.getId(), student1.getId());
        } catch (BadRequestException e) {
            failToDrop = true;
        }

        Assertions.assertThat(failToDrop).isTrue();
    }

    @Test
    public void test_EnrollmentService_DropOneCourse_WhenHasActiveENrollmentThenReturnsCorrectEnrollmentResponse() {
        when(studentService.findStudentById(student1.getId())).thenReturn(student1);
        when(courseRepository.existsById(course1.getId())).thenReturn(true);
        when(enrollmentRepository.findActiveEnrollment(course1.getId(), student1.getId())).thenReturn(Optional.ofNullable(enrollment1));
        enrollment1.setDropAt(Instant.now());
        when(enrollmentRepository.save(enrollment1)).thenReturn(enrollment1);

        EnrollmentRespond enrollmentRespond = enrollmentService.dropOneCourse(course1.getId(), student1.getId());

        Assertions.assertThat(enrollmentRespond).isEqualTo(
                enrollmentResponseMapper.apply(enrollment1)
        );
    }
}
