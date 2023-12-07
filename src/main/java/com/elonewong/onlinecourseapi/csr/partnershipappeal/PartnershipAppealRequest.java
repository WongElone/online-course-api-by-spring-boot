package com.elonewong.onlinecourseapi.csr.partnershipappeal;

import jakarta.validation.constraints.NotNull;

public record PartnershipAppealRequest(
    @NotNull
    String courseId
) {}
