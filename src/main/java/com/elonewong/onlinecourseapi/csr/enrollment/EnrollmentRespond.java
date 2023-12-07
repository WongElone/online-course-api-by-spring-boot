package com.elonewong.onlinecourseapi.csr.enrollment;

import com.elonewong.onlinecourseapi.csr.course.SimpleCourse;
import com.elonewong.onlinecourseapi.csr.student.Student;

import java.time.Instant;

public record EnrollmentRespond(
    SimpleCourse course,
    Student.SimpleStudent student,
    Instant enrollAt,
    Instant dropAt
) {}
