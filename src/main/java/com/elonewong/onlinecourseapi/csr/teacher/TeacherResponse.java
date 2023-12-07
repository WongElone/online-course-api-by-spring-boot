package com.elonewong.onlinecourseapi.csr.teacher;

import com.elonewong.onlinecourseapi.csr.course.SimpleCourse;
import com.elonewong.onlinecourseapi.csr.user.User;

import java.util.List;

public record TeacherResponse(
    String id,
    User.SimpleUser user,
    String profilePicture,
    List<SimpleCourse> courses
) {}
