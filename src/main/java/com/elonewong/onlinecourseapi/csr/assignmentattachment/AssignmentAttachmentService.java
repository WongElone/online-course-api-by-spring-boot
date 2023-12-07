package com.elonewong.onlinecourseapi.csr.assignmentattachment;

import com.elonewong.onlinecourseapi.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentAttachmentService {

    private AssignmentAttachmentRepository assignmentAttachmentRepository;

    @Autowired
    public AssignmentAttachmentService(AssignmentAttachmentRepository assignmentAttachmentRepository) {
        this.assignmentAttachmentRepository = assignmentAttachmentRepository;
    }

//    public List<AssignmentAttachment> findAttachmentsByIds(List<String> uploadIds) {
//        List<AssignmentAttachment> attachments = assignmentAttachmentRepository.findAllById(uploadIds);
//        assert (attachments.size() == uploadIds.size()) : new BadRequestException("Some Upload ids not found");
//        for (AssignmentAttachment attachment : attachments) {
//            if (attachment.getUpload().getToBeRemovedFromS3()) throw new BadRequestException("Upload of id " + attachment.getUpload().getId() + " is scheduled to be removed");
//        }
//        return attachments;
//    }

}
