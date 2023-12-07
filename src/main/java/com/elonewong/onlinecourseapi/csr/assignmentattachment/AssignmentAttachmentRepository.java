package com.elonewong.onlinecourseapi.csr.assignmentattachment;

import com.elonewong.onlinecourseapi.csr.custom.BaseUuidEntityRepository;
import com.elonewong.onlinecourseapi.csr.upload.Upload;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AssignmentAttachmentRepository extends BaseUuidEntityRepository<AssignmentAttachment> {

    @Query("SELECT aa FROM AssignmentAttachment aa WHERE aa.upload.id IN ?1")
    List<AssignmentAttachment> findByUploadsIds(List<String> uploadIds);

}
