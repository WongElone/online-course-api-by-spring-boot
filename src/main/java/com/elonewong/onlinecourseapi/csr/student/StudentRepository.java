package com.elonewong.onlinecourseapi.csr.student;

import com.elonewong.onlinecourseapi.csr.custom.BaseUuidEntityRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StudentRepository extends BaseUuidEntityRepository<Student> {

    @Query("SELECT s FROM Student s WHERE s.user.id = ?1")
    Optional<Student> findByUserId(String userId);

}
