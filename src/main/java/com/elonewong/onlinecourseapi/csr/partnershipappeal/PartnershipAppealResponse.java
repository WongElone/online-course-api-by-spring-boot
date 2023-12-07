package com.elonewong.onlinecourseapi.csr.partnershipappeal;

import com.elonewong.onlinecourseapi.csr.course.SimpleCourse;
import com.elonewong.onlinecourseapi.csr.teacher.Teacher;

import java.time.Instant;

public record PartnershipAppealResponse(
    Teacher.SimpleTeacher teacher,
    SimpleCourse course,
    Instant appealedAt
) {}
