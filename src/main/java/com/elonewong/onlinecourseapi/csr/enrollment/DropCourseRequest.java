package com.elonewong.onlinecourseapi.csr.enrollment;

import jakarta.validation.constraints.NotBlank;

public record DropCourseRequest(
    @NotBlank
    String courseId
) {}
