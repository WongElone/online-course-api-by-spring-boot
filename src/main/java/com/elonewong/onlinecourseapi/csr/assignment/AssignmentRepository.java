package com.elonewong.onlinecourseapi.csr.assignment;

import com.elonewong.onlinecourseapi.csr.custom.BaseUuidEntityRepository;
import com.elonewong.onlinecourseapi.csr.custom.CanGetEntityManager;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AssignmentRepository extends BaseUuidEntityRepository<Assignment> {

    @Query("SELECT a FROM Assignment a WHERE a.course.id = ?1")
    List<Assignment> findAssignmentsOfOneCourse(String courseId);

}
