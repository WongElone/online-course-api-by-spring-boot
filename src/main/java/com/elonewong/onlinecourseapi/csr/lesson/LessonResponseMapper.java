package com.elonewong.onlinecourseapi.csr.lesson;

import com.elonewong.onlinecourseapi.csr.chapter.Chapter;
import com.elonewong.onlinecourseapi.csr.course.SimpleCourse;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LessonResponseMapper implements Function<Lesson, LessonResponse> {

    @Override
    public LessonResponse apply(Lesson lesson) {
        Chapter chapter = lesson.getChapter();
        return new LessonResponse(
            lesson.getId(),
            new Chapter.SimpleChapter(
                chapter.getId(),
                new SimpleCourse(
                        chapter.getCourse().getId(),
                        chapter.getCourse().getTitle()
                ),
                chapter.getOrderInCourse(),
                chapter.getTitle()
            ),
            lesson.getOrderInChapter(),
            lesson.getTitle(),
            lesson.getWysiwyg(),
        (lesson.getVideo() == null) ? null : new LessonResponse.Video(
                lesson.getVideo().getUpload().getId()
            ),
            lesson.getAttachments().stream().map(attachment ->
                new LessonResponse.Attachment(
                attachment.getUpload().getId()
                )
            ).collect(Collectors.toList())
        );
    }

}
