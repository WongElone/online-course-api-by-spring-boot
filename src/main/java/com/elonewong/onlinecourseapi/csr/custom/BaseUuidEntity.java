package com.elonewong.onlinecourseapi.csr.custom;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;

@MappedSuperclass
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseUuidEntity {

    @Id
    @UuidGenerator
    @EqualsAndHashCode.Include
    @ToString.Include
    private String id;

}
