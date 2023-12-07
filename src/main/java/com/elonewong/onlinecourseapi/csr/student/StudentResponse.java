package com.elonewong.onlinecourseapi.csr.student;

import com.elonewong.onlinecourseapi.csr.course.SimpleCourse;
import com.elonewong.onlinecourseapi.csr.user.User;

import java.util.List;

public record StudentResponse(
    String id,
    User.SimpleUser user,
    String profilePicture,
    List<SimpleCourse> courses
) {}
