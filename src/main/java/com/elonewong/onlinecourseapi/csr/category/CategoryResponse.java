package com.elonewong.onlinecourseapi.csr.category;

import com.elonewong.onlinecourseapi.csr.course.SimpleCourse;

import java.util.List;
import java.util.Set;

public record CategoryResponse (
        String id,
        String name,
        List<SimpleCourse> courses
) {};
