package com.elonewong.onlinecourseapi.csr.lesson;

import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class LessonOrderMapper implements Function<Lesson, LessonOrder> {

    @Override
    public LessonOrder apply(Lesson lesson) {
        return new LessonOrder(
            lesson.getId(),
            lesson.getOrderInChapter()
        );
    }

}
