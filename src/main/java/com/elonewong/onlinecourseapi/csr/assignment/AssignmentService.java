package com.elonewong.onlinecourseapi.csr.assignment;

import com.elonewong.onlinecourseapi.csr.assignmentattachment.AssignmentAttachment;
import com.elonewong.onlinecourseapi.csr.assignmentattachment.AssignmentAttachmentRepository;
import com.elonewong.onlinecourseapi.csr.assignmentattachment.AssignmentAttachmentService;
import com.elonewong.onlinecourseapi.csr.course.Course;
import com.elonewong.onlinecourseapi.csr.course.CourseService;
import com.elonewong.onlinecourseapi.csr.upload.Upload;
import com.elonewong.onlinecourseapi.csr.upload.UploadRepository;
import com.elonewong.onlinecourseapi.csr.upload.UploadService;
import com.elonewong.onlinecourseapi.csr.user.Profile;
import com.elonewong.onlinecourseapi.exception.AuthorizationException;
import com.elonewong.onlinecourseapi.exception.BadRequestException;
import com.elonewong.onlinecourseapi.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AssignmentService {

    private AssignmentRepository assignmentRepository;
    private UploadRepository uploadRepository;
    private AssignmentResponseMapper assignmentResponseMapper;
    private CourseService courseService;
    private UploadService uploadService;
    private AssignmentAttachmentService assignmentAttachmentService;
    private AssignmentAttachmentRepository assignmentAttachmentRepository;

    @Autowired
    public AssignmentService(AssignmentRepository assignmentRepository, UploadRepository uploadRepository, AssignmentResponseMapper assignmentResponseMapper, CourseService courseService, UploadService uploadService, AssignmentAttachmentService assignmentAttachmentService, AssignmentAttachmentRepository assignmentAttachmentRepository) {
        this.assignmentRepository = assignmentRepository;
        this.uploadRepository = uploadRepository;
        this.assignmentResponseMapper = assignmentResponseMapper;
        this.courseService = courseService;
        this.uploadService = uploadService;
        this.assignmentAttachmentService = assignmentAttachmentService;
        this.assignmentAttachmentRepository = assignmentAttachmentRepository;
    }

    public List<AssignmentResponse> getAssignmentsOfOneCourse(String courseId, Profile userProfile) throws AuthorizationException {
        Course course = courseService.findCourseById(courseId);
        courseService.validateHavingAccessToCourse(userProfile, course);
        return assignmentRepository.findAssignmentsOfOneCourse(courseId)
                .stream().map(assignmentResponseMapper).collect(Collectors.toList());
    }

    @Transactional
    public AssignmentResponse addOneAssignmentToCourse(AssignmentRequest assignmentRequest, String courseId, Profile userProfile) throws AuthorizationException {
        Course course = courseService.findCourseById(courseId);
        courseService.validateIsTeacherOfTheCourse(userProfile, course);
        List<Upload> uploads = uploadService.findUploadsByIds(assignmentRequest.attachmentUploadsIds());
        uploads.forEach(upload -> {
            if (upload.getAttachedTo() != null) {
                throw new BadRequestException("Some uploads already belong to other entities");
            }
        });

        Assignment newAssignment = Assignment.builder()
                .title(assignmentRequest.title())
                .course(course)
                .wysiwyg(assignmentRequest.wysiwyg())
                .allowSubmission(assignmentRequest.allowSubmission())
                .visibleToStudent(assignmentRequest.visibleToStudent())
                .attachments(List.of())
                .build();

        Assignment assignment = assignmentRepository.save(newAssignment);
        List<AssignmentAttachment> newAttachments = new ArrayList<>();
        uploads.forEach(upload -> {
            upload.setAttachedTo(Upload.UploadAttachTo.AssignmentAttachment);
            newAttachments.add(
                AssignmentAttachment.builder()
                    .assignment(assignment)
                    .upload(upload)
                    .build()
            );
        });
        assignmentAttachmentRepository.saveAll(newAttachments);

        return assignmentResponseMapper.apply(assignment);
    }

    @Transactional
    public AssignmentResponse updateOneAssignment(String assignmentId, AssignmentRequest assignmentRequest, Profile userProfile) throws AuthorizationException {
        Assignment assignment = this.findOneAssignmentById(assignmentId);
        courseService.validateIsTeacherOfTheCourse(userProfile, assignment.getCourse());

        assignment.setTitle(assignmentRequest.title());
        assignment.setWysiwyg(assignmentRequest.wysiwyg());
        assignment.setAllowSubmission(assignmentRequest.allowSubmission());
        assignment.setVisibleToStudent(assignmentRequest.visibleToStudent());

        List<Upload> updateUploadsList = uploadService.findUploadsByIds(assignmentRequest.attachmentUploadsIds());
        // list of upload ids in request body of which uploads are assignment attachment uploads
        List<String> attachmentUploadsFoundInUpdateUploadsIdsList = new ArrayList<>();
        for (Upload upload : updateUploadsList) {
            if (upload.getAttachedTo() != null) {
                if (!Upload.UploadAttachTo.AssignmentAttachment.equals(upload.getAttachedTo())) {
                    throw new BadRequestException("Some uploads are attached to other entities instead of assignment attachment");
                }
                attachmentUploadsFoundInUpdateUploadsIdsList.add(upload.getId());
            }
        }
        if (
            assignmentAttachmentRepository.findByUploadsIds(attachmentUploadsFoundInUpdateUploadsIdsList).stream()
                .anyMatch(attachment -> !assignment.equals(attachment.getAssignment()))
        ) {
            throw new BadRequestException("Some uploads already belong to other assignment attachment");
        }

        // 1. create new assignment attachments, set upload to corresponding attachment
        Map<String, Upload> existingAttachmentsUploadsMap = new HashMap<>();
        assignment.getAttachments().forEach(attachment -> {
            existingAttachmentsUploadsMap.put(attachment.getUpload().getId(), attachment.getUpload());
        });

        // 2.1 create a list of attachments for the uploads which are not in existing attachments but required in the request body

        List<AssignmentAttachment> newAttachmentsList = new ArrayList<>();
        updateUploadsList.forEach(upload -> {
            if (!existingAttachmentsUploadsMap.containsKey(upload.getId())) {
                upload.setAttachedTo(Upload.UploadAttachTo.AssignmentAttachment);
                newAttachmentsList.add(
                    AssignmentAttachment.builder()
                        .assignment(assignment)
                        .upload(upload)
                        .build()
                );
            }
        });
        // 2.2 save the new attachments to db
        assignmentAttachmentRepository.saveAll(newAttachmentsList);

        // 3 remove old assignment attachments and uploads
        List<AssignmentAttachment> toBeRemovedAttachments = assignment.getAttachments().stream()
            .filter(attachment -> !updateUploadsList.contains(attachment.getUpload()))
            .collect(Collectors.toList());

        List<Upload> uploadsToBeRemovedFromS3 = toBeRemovedAttachments.stream()
                .map(AssignmentAttachment::getUpload)
                .peek(upload -> {
                    upload.setToBeRemovedFromS3(true);
                    upload.setAttachedTo(null);
                })
                .collect(Collectors.toList());

        uploadRepository.saveAll(uploadsToBeRemovedFromS3);
        assignmentAttachmentRepository.deleteAllInBatch(toBeRemovedAttachments);

        return assignmentResponseMapper.apply(assignment);
    }

    @Transactional
    public void deleteOneAssignment(String assignmentId, Profile userProfile) throws AuthorizationException {
        Assignment assignment = this.findOneAssignmentById(assignmentId);
        Course course = assignment.getCourse();
        courseService.validateIsTeacherOfTheCourse(userProfile, course);

        List<Upload> uploadsToBeRemovedFromS3 = assignment.getAttachments().stream()
                .map(AssignmentAttachment::getUpload)
                .peek(upload -> {
                    upload.setToBeRemovedFromS3(true);
                    upload.setAttachedTo(null);
                })
                .collect(Collectors.toList());

        uploadRepository.saveAll(uploadsToBeRemovedFromS3);
        assignmentRepository.delete(assignment);
    }

    public Assignment findOneAssignmentById(String assignmentId) {
        return assignmentRepository.findById(assignmentId).orElseThrow(() -> new ResourceNotFoundException("Assignment Id " + assignmentId + " not found"));
    }

}
