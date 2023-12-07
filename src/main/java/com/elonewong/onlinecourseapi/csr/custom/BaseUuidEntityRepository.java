package com.elonewong.onlinecourseapi.csr.custom;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BaseUuidEntityRepository<T extends BaseUuidEntity> extends JpaRepository<T, String> {
}
