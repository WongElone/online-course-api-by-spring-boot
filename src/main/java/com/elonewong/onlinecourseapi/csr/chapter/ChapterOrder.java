package com.elonewong.onlinecourseapi.csr.chapter;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChapterOrder(
    @NotBlank
    String chapterId,
    @NotNull
    @Min(1)
    Integer orderNum
) {}
