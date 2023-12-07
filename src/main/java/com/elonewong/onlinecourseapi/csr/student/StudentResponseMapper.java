package com.elonewong.onlinecourseapi.csr.student;

import com.elonewong.onlinecourseapi.csr.course.SimpleCourse;
import com.elonewong.onlinecourseapi.csr.user.User;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StudentResponseMapper implements Function<Student, StudentResponse> {

    @Override
    public StudentResponse apply(Student student) {
        User u = student.getUser();
        return new StudentResponse(
            student.getId(),
            new User.SimpleUser(
                u.getId(),
                    u.getEmail(),
                    u.getFirstName(),
                    u.getLastName(),
                    u.getRole()
            ),
            student.getProfilePicture(),
            student.getCourses()
                    .stream().map(course -> new SimpleCourse(
                        course.getId(),
                        course.getTitle()
                    )).collect(Collectors.toList())
        );
    }
}
