package com.elonewong.onlinecourseapi.csr.enrollment;

import com.elonewong.onlinecourseapi.csr.custom.BaseUuidEntityRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends BaseUuidEntityRepository<Enrollment> {

    @Query("SELECT e FROM Enrollment e WHERE e.course.id = ?1 AND e.student.id = ?2 AND e.dropAt = NULL")
    Optional<Enrollment> findActiveEnrollment(String courseId, String studentId);

    @Query("SELECT e FROM Enrollment e WHERE e.student.id = ?1")
    List<Enrollment> findEnrollmentsByStudentId(String studentId);

}
