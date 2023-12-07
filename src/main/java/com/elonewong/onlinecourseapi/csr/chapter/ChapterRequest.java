package com.elonewong.onlinecourseapi.csr.chapter;

import jakarta.validation.constraints.NotBlank;

public record ChapterRequest (
    @NotBlank
    String title
) {}
