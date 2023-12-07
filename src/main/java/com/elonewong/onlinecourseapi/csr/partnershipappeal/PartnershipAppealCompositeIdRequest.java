package com.elonewong.onlinecourseapi.csr.partnershipappeal;

import jakarta.validation.constraints.NotBlank;

public record PartnershipAppealCompositeIdRequest(
    @NotBlank
    String courseId,
    @NotBlank
    String joinTeacherId
) {}
