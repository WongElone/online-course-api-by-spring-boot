package com.elonewong.onlinecourseapi.csr.assignment;

import com.elonewong.onlinecourseapi.csr.custom.BaseUuidEntity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentAuditable extends BaseUuidEntity {

    @CreatedDate
    Instant createdAt;

    @LastModifiedDate
    Instant updatedAt;

}
