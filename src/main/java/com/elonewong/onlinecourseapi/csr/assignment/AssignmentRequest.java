package com.elonewong.onlinecourseapi.csr.assignment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AssignmentRequest(
    @NotBlank
    String title,
    String wysiwyg,
    @NotNull
    Boolean visibleToStudent,
    @NotNull
    Boolean allowSubmission,
    @NotNull
    List<String> attachmentUploadsIds
) {}
