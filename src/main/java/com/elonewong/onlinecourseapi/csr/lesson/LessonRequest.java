package com.elonewong.onlinecourseapi.csr.lesson;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record LessonRequest(
    @NotNull
    String chapterId,
    @NotBlank
    String title,
    String wysiwyg,
    @Valid
    Video video,
    @NotNull
    List<@Valid Attachment> attachments
) {
    public record Video(
        @NotBlank
        String uploadId
    ) {}

    public record Attachment(
        @NotBlank
        String uploadId
    ) {}
}
