package com.elonewong.onlinecourseapi.csr.assignment;

import java.util.List;

public record AssignmentResponse(
    String id,
    String title,
    String wysiwyg,
    Boolean visibleToStudent,
    Boolean allowSubmission,
    List<String> attachmentsUploadIds
) {}
