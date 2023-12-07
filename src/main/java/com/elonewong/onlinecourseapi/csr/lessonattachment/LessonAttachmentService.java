package com.elonewong.onlinecourseapi.csr.lessonattachment;

import com.elonewong.onlinecourseapi.csr.upload.UploadRepository;
import com.elonewong.onlinecourseapi.csr.upload.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LessonAttachmentService {

    private LessonAttachmentRepository lessonAttachmentRepository;
    private UploadRepository uploadRepository;
    private UploadService uploadService;

    @Autowired
    public LessonAttachmentService(LessonAttachmentRepository lessonAttachmentRepository, UploadRepository uploadRepository, UploadService uploadService) {
        this.lessonAttachmentRepository = lessonAttachmentRepository;
        this.uploadRepository = uploadRepository;
        this.uploadService = uploadService;
    }

}
