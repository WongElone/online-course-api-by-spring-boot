package com.elonewong.onlinecourseapi.csr.enrollment;

import com.elonewong.onlinecourseapi.common.PaymentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentPayment {

    @NotNull
    @PositiveOrZero
    private Integer amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentType type;

    @NotBlank
    @Size(max = 1000)
    private String description;

}
