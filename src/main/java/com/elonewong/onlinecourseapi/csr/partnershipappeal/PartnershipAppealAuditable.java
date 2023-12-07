package com.elonewong.onlinecourseapi.csr.partnershipappeal;

import com.elonewong.onlinecourseapi.csr.custom.BaseUuidEntity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartnershipAppealAuditable {

    @CreatedDate
    @NotNull
    Instant appealedAt;

}
