package com.elonewong.onlinecourseapi.csr.course;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;

public record CourseRequest (
        @NotBlank @Size(min = 3, max = 255)
        String title,
        @NotNull
        List<String> categoryIds
) {}
