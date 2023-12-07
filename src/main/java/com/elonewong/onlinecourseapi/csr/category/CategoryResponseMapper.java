package com.elonewong.onlinecourseapi.csr.category;

import com.elonewong.onlinecourseapi.csr.course.SimpleCourse;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CategoryResponseMapper implements Function<Category, CategoryResponse> {
    @Override
    public CategoryResponse apply(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getCourses().stream()
                        .map(course -> new SimpleCourse(course.getId(), course.getTitle()))
                        .collect(Collectors.toList())
        );
    }
}
