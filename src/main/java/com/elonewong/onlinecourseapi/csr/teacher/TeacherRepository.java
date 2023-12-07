package com.elonewong.onlinecourseapi.csr.teacher;

import com.elonewong.onlinecourseapi.csr.custom.BaseUuidEntityRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TeacherRepository extends BaseUuidEntityRepository<Teacher> {

    @Query("SELECT t FROM Teacher t WHERE t.user.id = ?1")
    Optional<Teacher> findByUserId(String userId);

}
