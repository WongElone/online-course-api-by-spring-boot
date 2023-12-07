package com.elonewong.onlinecourseapi.csr.assignment;

import com.elonewong.onlinecourseapi.csr.assignmentattachment.AssignmentAttachment;
import com.elonewong.onlinecourseapi.csr.upload.Upload;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AssignmentResponseMapper implements Function<Assignment, AssignmentResponse> {
    @Override
    public AssignmentResponse apply(Assignment assignment) {
        return new AssignmentResponse(
            assignment.getId(),
            assignment.getTitle(),
            assignment.getWysiwyg(),
            assignment.getVisibleToStudent(),
            assignment.getAllowSubmission(),
            assignment.getAttachments().stream()
                .map(attachment -> attachment.getUpload().getId())
                .collect(Collectors.toList())
        );
    }
}
