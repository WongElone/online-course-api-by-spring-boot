package com.elonewong.onlinecourseapi.csr.course;

import com.elonewong.onlinecourseapi.csr.category.SimpleCategory;
import com.elonewong.onlinecourseapi.csr.teacher.Teacher;

import java.util.List;

public record CourseResponse (
        String id,
        String title,
        List<SimpleCategory> categories,
        List<Teacher.SimpleTeacher> teachers,
        Integer studentsCount
) {}
