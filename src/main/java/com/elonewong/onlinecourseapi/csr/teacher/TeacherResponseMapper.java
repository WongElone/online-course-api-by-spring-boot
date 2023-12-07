package com.elonewong.onlinecourseapi.csr.teacher;

import com.elonewong.onlinecourseapi.csr.course.SimpleCourse;
import com.elonewong.onlinecourseapi.csr.user.User;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TeacherResponseMapper implements Function<Teacher, TeacherResponse> {

    @Override
    public TeacherResponse apply(Teacher teacher) {
        User u = teacher.getUser();
        return new TeacherResponse(
            teacher.getId(),
            new User.SimpleUser(
                u.getId(),
                    u.getEmail(),
                    u.getFirstName(),
                    u.getLastName(),
                    u.getRole()
            ),
            teacher.getProfilePicture(),
            teacher.getCourses()
                    .stream().map(course -> new SimpleCourse(
                            course.getId(),
                            course.getTitle()
                    )).collect(Collectors.toList())
        );
    }

}
