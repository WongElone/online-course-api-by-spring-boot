package com.elonewong.onlinecourseapi.csr.enrollment;

import com.elonewong.onlinecourseapi.csr.course.Course;
import com.elonewong.onlinecourseapi.csr.course.SimpleCourse;
import com.elonewong.onlinecourseapi.csr.student.Student;
import com.elonewong.onlinecourseapi.csr.user.User;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class EnrollmentResponseMapper implements Function<Enrollment, EnrollmentRespond> {

    @Override
    public EnrollmentRespond apply(Enrollment enrollment) {
        Course course = enrollment.getCourse();
        Student student = enrollment.getStudent();
        User user = student.getUser();
        return new EnrollmentRespond(
                new SimpleCourse(
                        course.getId(),
                        course.getTitle()
                ),
                new Student.SimpleStudent(
                        student.getId(),
                        new User.SimpleUser(
                                user.getId(),
                                user.getEmail(),
                                user.getFirstName(),
                                user.getLastName(),
                                user.getRole()
                        )
                ),
                enrollment.getEnrollAt(),
                enrollment.getDropAt()
        );
    }

}
