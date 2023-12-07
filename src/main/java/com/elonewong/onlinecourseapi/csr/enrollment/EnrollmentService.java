package com.elonewong.onlinecourseapi.csr.enrollment;

import com.elonewong.onlinecourseapi.csr.course.Course;
import com.elonewong.onlinecourseapi.csr.course.CourseRepository;
import com.elonewong.onlinecourseapi.csr.course.CourseService;
import com.elonewong.onlinecourseapi.csr.student.Student;
import com.elonewong.onlinecourseapi.csr.student.StudentRepository;
import com.elonewong.onlinecourseapi.csr.student.StudentService;
import com.elonewong.onlinecourseapi.exception.BadRequestException;
import com.elonewong.onlinecourseapi.exception.ResourceNotFoundException;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
public class EnrollmentService {

    private EnrollmentRepository enrollmentRepository;
    private StudentRepository studentRepository;
    private CourseRepository courseRepository;
    private EnrollmentResponseMapper enrollmentResponseMapper;
    private CourseService courseService;
    private StudentService studentService;

    @Autowired
    public EnrollmentService(EnrollmentRepository enrollmentRepository, StudentRepository studentRepository, CourseRepository courseRepository, EnrollmentResponseMapper enrollmentResponseMapper, CourseService courseService, StudentService studentService) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentResponseMapper = enrollmentResponseMapper;
        this.courseService = courseService;
        this.studentService = studentService;
    }

    @Transactional
    public EnrollmentRespond enrollOneCourse(EnrollCourseRequest enrollCourseRequest, String studentId) {
        String courseId = enrollCourseRequest.courseId();
        Course course = courseService.findCourseById(courseId);
        Student student = studentService.findStudentById(studentId);
        if (enrollmentRepository.findActiveEnrollment(courseId, studentId).isPresent()) {
            throw new BadRequestException("already enrolled");
        }
        // update student's courses
        List<Course> enrolledCourses = student.getCourses();
        System.out.println(enrolledCourses.getClass());
        enrolledCourses.add(course);
        student.setCourses(enrolledCourses);
        studentRepository.save(student);

        EnrollCourseRequest.EnrollmentPaymentRequest payment = enrollCourseRequest.payment();
        return enrollmentResponseMapper.apply(
            enrollmentRepository.save(Enrollment.builder()
                .course(course)
                .student(student)
                .payment(EnrollmentPayment.builder()
                        .amount(payment.amount())
                        .type(payment.type())
                        .description(payment.amount() + " was paid by " + payment.type())
                        .build()
                )
                .build()
        ));
    }

    @Transactional
    public EnrollmentRespond dropOneCourse(String courseId, String studentId) {
        Student student = studentService.findStudentById(studentId);
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("course id not found");
        }
        Enrollment activeEnrollment = enrollmentRepository.findActiveEnrollment(courseId, studentId)
                .orElseThrow(() -> new BadRequestException("you are not actively enrolled to this course"));
        activeEnrollment.setDropAt(Instant.now());
        // update student's courses list
        student.setCourses(
            student.getCourses().stream()
                    .filter(course -> !course.getId().equals(courseId))
                    .collect(Collectors.toList())
        );
        studentRepository.save(student);

        return enrollmentResponseMapper.apply(
                enrollmentRepository.save(activeEnrollment)
        );
    }

    public List<EnrollmentRespond> getEnrollmentsByStudentId(String studentId) {
        return enrollmentRepository.findEnrollmentsByStudentId(studentId)
                .stream().map(enrollmentResponseMapper).collect(Collectors.toList());
    }

}
