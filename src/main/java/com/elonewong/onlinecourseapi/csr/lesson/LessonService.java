package com.elonewong.onlinecourseapi.csr.lesson;

import com.elonewong.onlinecourseapi.csr.chapter.Chapter;
import com.elonewong.onlinecourseapi.csr.chapter.ChapterRepository;
import com.elonewong.onlinecourseapi.csr.chapter.ChapterService;
import com.elonewong.onlinecourseapi.csr.course.Course;
import com.elonewong.onlinecourseapi.csr.course.CourseService;
import com.elonewong.onlinecourseapi.csr.lessonattachment.LessonAttachment;
import com.elonewong.onlinecourseapi.csr.lessonattachment.LessonAttachmentRepository;
import com.elonewong.onlinecourseapi.csr.lessonvideo.LessonVideo;
import com.elonewong.onlinecourseapi.csr.lessonvideo.LessonVideoRepository;
import com.elonewong.onlinecourseapi.csr.lessonvideo.LessonVideoService;
import com.elonewong.onlinecourseapi.csr.upload.Upload;
import com.elonewong.onlinecourseapi.csr.upload.UploadRepository;
import com.elonewong.onlinecourseapi.csr.upload.UploadService;
import com.elonewong.onlinecourseapi.csr.user.Profile;
import com.elonewong.onlinecourseapi.exception.AuthorizationException;
import com.elonewong.onlinecourseapi.exception.BadRequestException;
import com.elonewong.onlinecourseapi.exception.ResourceNotFoundException;
import com.elonewong.onlinecourseapi.util.ListUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LessonService {

    private CourseService courseService;
    private LessonRepository lessonRepository;
    private LessonResponseMapper lessonResponseMapper;
    private ChapterService chapterService;
    private ChapterRepository chapterRepository;
    private UploadService uploadService;
    private LessonVideoService lessonVideoService;
    private LessonVideoRepository lessonVideoRepository;
    private LessonAttachmentRepository lessonAttachmentRepository;
    private UploadRepository uploadRepository;

    @Autowired
    public LessonService(CourseService courseService, LessonRepository lessonRepository, LessonResponseMapper lessonResponseMapper, ChapterService chapterService, ChapterRepository chapterRepository, UploadService uploadService, LessonVideoService lessonVideoService, LessonVideoRepository lessonVideoRepository, LessonAttachmentRepository lessonAttachmentRepository, UploadRepository uploadRepository) {
        this.courseService = courseService;
        this.lessonRepository = lessonRepository;
        this.lessonResponseMapper = lessonResponseMapper;
        this.chapterService = chapterService;
        this.chapterRepository = chapterRepository;
        this.uploadService = uploadService;
        this.lessonVideoService = lessonVideoService;
        this.lessonVideoRepository = lessonVideoRepository;
        this.lessonAttachmentRepository = lessonAttachmentRepository;
        this.uploadRepository = uploadRepository;
    }

    public LessonResponse getOneLesson(String lessonId, String courseId, Profile userProfile) throws AuthorizationException {
        Lesson lesson = this.findOneLessonById(lessonId);
        Course courseOfLesson = lesson.getChapter().getCourse();
        if (!courseOfLesson.getId().equals(courseId)) {
            throw new ResourceNotFoundException("Either Course id not found or this lesson not belongs to this course");
        }
//        else {
//            try {
//                courseService.findCourseById(courseId);
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw new ResourceNotFoundException("Either Course id not found, Lesson not belongs to this course or Lesson id not found");
//            }
//        }

        courseService.validateHavingAccessToCourse(userProfile, courseOfLesson);

        return lessonResponseMapper.apply(lesson);
    }

    private Lesson findOneLessonById(String lessonId) {
        return lessonRepository.findById(lessonId).orElseThrow(
            () -> new ResourceNotFoundException("Lesson id not found"));
    }

    @Transactional
    public void deleteOneLesson(String lessonId, String courseId, Profile userProfile) throws AuthorizationException {
        Lesson lesson = this.findOneLessonById(lessonId);
        Course courseOfLesson = lesson.getChapter().getCourse();
        if (!courseOfLesson.getId().equals(courseId)) {
            throw new ResourceNotFoundException("Either Course id not found or this lesson not belongs to this course");
        }

        courseService.validateIsTeacherOfTheCourse(userProfile, courseOfLesson);

        List<Upload> abandonedUploads = new ArrayList<>();
        LessonVideo video = lesson.getVideo();
        if (video != null) {
            Upload upload = video.getUpload();
            upload.setAttachedTo(null);
            upload.setToBeRemovedFromS3(true);
            abandonedUploads.add(upload);
        }

        List<LessonAttachment> attachments = lesson.getAttachments();
        if (!attachments.isEmpty()) {
            attachments.forEach(attachment -> {
                Upload upload = attachment.getUpload();
                upload.setAttachedTo(null);
                upload.setToBeRemovedFromS3(true);
                abandonedUploads.add(upload);
            });
        }

        uploadRepository.saveAll(abandonedUploads);

        lessonRepository.delete(lesson);
    }

    @Transactional
    public LessonResponse addOneLesson(String courseId, LessonRequest lessonRequest, Profile userProfile) throws AuthorizationException {
        Chapter chapter = chapterService.findOneChapterOfACourse(courseId, lessonRequest.chapterId());
        courseService.validateIsTeacherOfTheCourse(userProfile, chapter.getCourse());

        Lesson newLesson = Lesson.builder()
                .chapter(chapter)
                .title(lessonRequest.title())
                .wysiwyg(lessonRequest.wysiwyg())
                .orderInChapter(chapter.getLessons().size() + 1)
                .attachments(new ArrayList<>())
                .build();
        lessonRepository.save(newLesson);

        // create and save lesson video
        LessonRequest.Video videoRequest = lessonRequest.video();
        if (videoRequest != null) {
            Upload upload = uploadService.findOneUploadById(videoRequest.uploadId());
            if (upload.getAttachedTo() != null) {
                throw new BadRequestException("Upload of id " + upload.getId() + " already belongs to other entities");
            }
            upload.setAttachedTo(Upload.UploadAttachTo.LessonVideo);
            LessonVideo video = LessonVideo.builder()
                    .lesson(newLesson)
                    .upload(upload)
                    .build();
            newLesson.setVideo(
                lessonVideoRepository.save(video)
            );
        }

        // create and save lesson attachments
        List<LessonRequest.Attachment> attachmentsRequest = lessonRequest.attachments();
        if (attachmentsRequest != null && !attachmentsRequest.isEmpty()) {
            List<String> attachmentsUploadsIds = attachmentsRequest.stream().map(LessonRequest.Attachment::uploadId).collect(Collectors.toList());
            if (ListUtil.containsDuplicatedElem(attachmentsUploadsIds)) {
                throw new BadRequestException("Duplicated entries of attachments upload ids in request body");
            }
            List<Upload> uploads = uploadService.findUploadsByIds(attachmentsUploadsIds);
            List<LessonAttachment> attachments = uploads.stream().map(upload -> {
                upload.setAttachedTo(Upload.UploadAttachTo.LessonAttachment);
                return LessonAttachment.builder()
                        .lesson(newLesson)
                        .upload(upload)
                        .build();
            }).collect(Collectors.toList());
            newLesson.getAttachments().addAll(
                lessonAttachmentRepository.saveAll(attachments)
            );
        }

        // return lesson response
        return lessonResponseMapper.apply(newLesson);
    }

    @Transactional
    public LessonResponse updateOneLesson(String lessonId, String courseId, LessonRequest lessonRequest, Profile userProfile) throws AuthorizationException {
        Lesson lesson = this.findOneLessonById(lessonId);
        Chapter originalChapter = lesson.getChapter();
        Course courseOfLesson = originalChapter.getCourse();
        if (!courseOfLesson.getId().equals(courseId)) {
            throw new ResourceNotFoundException("Either Course id not found or this lesson not belongs to this course");
        }

        courseService.validateIsTeacherOfTheCourse(userProfile, courseOfLesson);

        //// update lesson content ////
        lesson.setTitle(lessonRequest.title());
        lesson.setWysiwyg(lessonRequest.wysiwyg());
        // update lesson video //
        List<Upload> abandonedUploads = new ArrayList<>();
        LessonRequest.Video videoRequest = lessonRequest.video();
        Upload existingLessonVideoUpload = null;
        if (lesson.getVideo() != null) {
            existingLessonVideoUpload = lesson.getVideo().getUpload();
        }
        if (videoRequest != null) {
            Upload upload = uploadService.findOneUploadById(videoRequest.uploadId());
            // is this upload already belong to other entities?
            if (upload.getAttachedTo() != null) {
                if (!Upload.UploadAttachTo.LessonVideo.equals(upload.getAttachedTo())) {
                    throw new BadRequestException("Upload of id " + upload.getId() + " belong to other entity instead of LessonVideo");
                }
                if (!upload.equals(existingLessonVideoUpload)) {
                    throw new BadRequestException("Upload of id " + upload.getId() + " belong to other lesson video");
                }
            } else {
                if (existingLessonVideoUpload != null) abandonedUploads.add(existingLessonVideoUpload);
                upload.setAttachedTo(Upload.UploadAttachTo.LessonVideo);
                LessonVideo newVideo = LessonVideo.builder()
                        .lesson(lesson)
                        .upload(upload)
                        .build();
                lesson.setVideo(newVideo);
            }
        } else {
            if (existingLessonVideoUpload != null) abandonedUploads.add(existingLessonVideoUpload);
            lesson.setVideo(null);
        }
        // end of update lesson video //
        // update lesson attachments //
        List<LessonRequest.Attachment> attachmentsRequest = lessonRequest.attachments();
        ArrayList<LessonAttachment> abandonedLessonAttachments = new ArrayList<>(lesson.getAttachments());
        if (!attachmentsRequest.isEmpty()) {
            Map<Upload, LessonAttachment> oldAttachmentsUploadsMap = new HashMap<>();
            lesson.getAttachments().forEach(attachment -> {
                oldAttachmentsUploadsMap.put(attachment.getUpload(), attachment);
            });
            // find all attachments request uploads
            List<String> requestedUploadsIds = attachmentsRequest.stream().map(LessonRequest.Attachment::uploadId).collect(Collectors.toList());
            if (ListUtil.containsDuplicatedElem(requestedUploadsIds)) {
                throw new BadRequestException("Duplicated entries of attachments upload ids in request body");
            }
            List<Upload> requestedUploads = uploadService.findUploadsByIds(requestedUploadsIds);
            List<Upload> orphanUploads = new ArrayList<>();
            for (Upload upload : requestedUploads) {
                if (upload.getAttachedTo() != null) {
                    if (!Upload.UploadAttachTo.LessonAttachment.equals(upload.getAttachedTo())) {
                        throw new BadRequestException("Upload of id " + upload.getId() + " belong to other entity instead of LessonAttachment");
                    }
                    if (!oldAttachmentsUploadsMap.containsKey(upload)) {
                        throw new BadRequestException("Upload of id " + upload.getId() + " belong to other lesson attachment");
                    }
                    oldAttachmentsUploadsMap.remove(upload);
                } else {
                    orphanUploads.add(upload);
                }
            }

            // create new attachments and add them to lesson
            List<LessonAttachment> newAttachments = orphanUploads.stream().map(upload -> {
                upload.setAttachedTo(Upload.UploadAttachTo.LessonAttachment);
                LessonAttachment attachment = LessonAttachment.builder()
                        .upload(upload)
                        .lesson(lesson)
                        .build();
                lesson.getAttachments().add(attachment);
                return attachment;
            }).collect(Collectors.toList());
            oldAttachmentsUploadsMap.forEach((upload, attachment) -> {
                // add uploads of abandoned attachments to abandoned uploads list
                abandonedUploads.add(upload);
                // remove abandoned attachments from lesson
                lesson.getAttachments().remove(attachment);
            });
        } else {
            Iterator<LessonAttachment> iterator = lesson.getAttachments().iterator();
            while (iterator.hasNext()) {
                // add uploads of abandoned attachments to abandoned uploads list
                abandonedUploads.add(iterator.next().getUpload());
                // remove abandoned attachments from lesson
                iterator.remove();
            }
        }
        // end of update lesson attachments //
        // update abandoned uploads
        if (!abandonedUploads.isEmpty()) {
            abandonedUploads.forEach(upload -> {
                upload.setAttachedTo(null);
                upload.setToBeRemovedFromS3(true);
            });
            uploadRepository.saveAll(abandonedUploads);
        }

        // if chapter id in request body is the original chapter id, then save above changes and return
        if (lessonRequest.chapterId().equals(originalChapter.getId())) {
            return lessonResponseMapper.apply(lessonRepository.save(lesson));
        }
        //// End of update lesson content ////

        //// Below is executed when the chapter id is different, i.e. lesson is being transferred to another chapter ////
        Chapter chapterToBeJoined = chapterService.findOneChapterOfACourse(courseId, lessonRequest.chapterId());
        lesson.setChapter(chapterToBeJoined);
        Integer originalOrderInChapter = lesson.getOrderInChapter();
        lesson.setOrderInChapter(chapterToBeJoined.getLessons().size() + 1);

        // update lessons list of original chapter (i.e. remove the transferred lesson)
        List<Lesson> updatedLessonsOfOriginalChapter = new ArrayList<>();
        for (Lesson l : originalChapter.getLessons()) {
            if (l.getOrderInChapter().equals(originalOrderInChapter))
                continue;
            if (l.getOrderInChapter() > originalOrderInChapter) {
                l.setOrderInChapter(l.getOrderInChapter() - 1);
            }
            updatedLessonsOfOriginalChapter.add(l);
        }
        originalChapter.setLessons(updatedLessonsOfOriginalChapter);
        // end of update lesson list of original chapter

        Lesson updatedLesson = lessonRepository.save(lesson);
        chapterRepository.save(originalChapter);

        return lessonResponseMapper.apply(updatedLesson);
    }
}
