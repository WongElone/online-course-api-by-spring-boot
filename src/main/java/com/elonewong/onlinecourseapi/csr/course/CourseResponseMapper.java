package com.elonewong.onlinecourseapi.csr.course;

import com.elonewong.onlinecourseapi.csr.category.SimpleCategory;
import com.elonewong.onlinecourseapi.csr.teacher.Teacher;
import com.elonewong.onlinecourseapi.csr.user.User;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CourseResponseMapper implements Function<Course, CourseResponse> {
    @Override
    public CourseResponse apply(Course course) {
        return genResponse(course, null);
    }

    public CourseResponse genResponse(Course course, Integer studentCounts) {
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getCategories().stream()
                        .map(category -> new SimpleCategory(
                                category.getId(),
                                category.getName()
                        ))
                        .collect(Collectors.toList()),
                course.getTeachers().stream()
                        .map(teacher -> {
                            User u = teacher.getUser();
                            return new Teacher.SimpleTeacher(
                                    teacher.getId(),
                                    new User.SimpleUser(
                                            u.getId(),
                                            u.getEmail(),
                                            u.getFirstName(),
                                            u.getLastName(),
                                            u.getRole()
                                    )
                            );
                        })
                        .collect(Collectors.toList()),
                studentCounts
        );
    }
}
