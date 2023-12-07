package com.elonewong.onlinecourseapi.csr.lesson;

import com.elonewong.onlinecourseapi.csr.chapter.Chapter;
import com.elonewong.onlinecourseapi.csr.course.SimpleCourse;

import java.util.List;

public record LessonResponse (
    String id,
    Chapter.SimpleChapter chapter,
    Integer orderInChapter,
    String title,
    String wysiwyg,
    Video video,
    List<Attachment> attachments

) {
    public record Video(
        String uploadId
    ) {}

    public record Attachment(
        String uploadId
    ) {}
}
