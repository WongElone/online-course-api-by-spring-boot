package com.elonewong.onlinecourseapi.csr.chapter;

import com.elonewong.onlinecourseapi.csr.course.SimpleCourse;
import com.elonewong.onlinecourseapi.csr.lesson.Lesson;

import java.util.List;

public record ChapterResponse (
    String chapterId,
    String title,
    SimpleCourse course,
    Integer orderInCourse,
    List<Lesson.SimpleLesson> lessons

) {}
