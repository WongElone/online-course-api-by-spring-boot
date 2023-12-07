package com.elonewong.onlinecourseapi.csr.chapter;

import com.elonewong.onlinecourseapi.csr.course.SimpleCourse;
import com.elonewong.onlinecourseapi.csr.lesson.Lesson;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ChapterResponseMapper implements Function<Chapter, ChapterResponse> {

    @Override
    public ChapterResponse apply(Chapter chapter) {
        return new ChapterResponse(
            chapter.getId(),
            chapter.getTitle(),
            new SimpleCourse(
                chapter.getCourse().getId(),
                chapter.getCourse().getTitle()
            ),
            chapter.getOrderInCourse(),
            chapter.getLessons().stream().map(lesson ->
                new Lesson.SimpleLesson(
                    lesson.getId(),
                    lesson.getTitle(),
                    lesson.getOrderInChapter()
                )
            ).collect(Collectors.toList())
        );
    }

}
