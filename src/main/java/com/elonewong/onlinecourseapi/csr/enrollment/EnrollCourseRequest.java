package com.elonewong.onlinecourseapi.csr.enrollment;

import com.elonewong.onlinecourseapi.common.PaymentType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record EnrollCourseRequest(
    @NotBlank
    String courseId,
    @NotNull
    @Valid
    EnrollmentPaymentRequest payment

) {
    public record EnrollmentPaymentRequest(
        @NotNull
        @PositiveOrZero
        Integer amount,
        @NotNull
        @Enumerated(EnumType.STRING)
        PaymentType type
    ) {}
}
