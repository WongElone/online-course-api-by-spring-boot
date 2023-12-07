package com.elonewong.onlinecourseapi.csr.partnershipappeal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PartnershipAppealRepository extends JpaRepository<PartnershipAppeal, PartnershipAppealCompositeId> {

    @Query("SELECT pa FROM PartnershipAppeal pa WHERE pa.course.id = ?1 ORDER BY pa.appealedAt DESC")
    List<PartnershipAppeal> findAllAppealsOfOneCourse(String courseId);

    @Query("SELECT pa FROM PartnershipAppeal pa WHERE pa.teacher.id = ?1 ORDER BY pa.appealedAt DESC")
    List<PartnershipAppeal> findAllAppealsOfOneTeacher(String teacherId);

}
