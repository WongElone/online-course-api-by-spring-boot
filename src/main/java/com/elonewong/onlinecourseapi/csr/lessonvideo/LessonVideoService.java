package com.elonewong.onlinecourseapi.csr.lessonvideo;

import com.elonewong.onlinecourseapi.csr.lesson.LessonRepository;
import com.elonewong.onlinecourseapi.csr.upload.UploadRepository;
import com.elonewong.onlinecourseapi.csr.upload.UploadService;
import com.elonewong.onlinecourseapi.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LessonVideoService {

    private LessonVideoRepository lessonVideoRepository;
    private UploadRepository uploadRepository;
    private UploadService uploadService;

    @Autowired
    public LessonVideoService(LessonVideoRepository lessonVideoRepository, UploadRepository uploadRepository, UploadService uploadService) {
        this.lessonVideoRepository = lessonVideoRepository;
        this.uploadRepository = uploadRepository;
        this.uploadService = uploadService;
    }

    public LessonVideo findLessonVideoByUploadId(String uploadId) {
        return lessonVideoRepository.findByUploadId(uploadId).orElseThrow(() -> new ResourceNotFoundException("Lesson video with upload_id " + uploadId + " not found"));
    }

}
