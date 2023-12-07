package com.elonewong.onlinecourseapi.csr.lesson;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LessonOrder (
    @NotBlank
    String lessonId,
    @NotNull
    @Min(1)
    Integer orderNum
) {}
