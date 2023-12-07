package com.elonewong.onlinecourseapi.csr.category;

import com.elonewong.onlinecourseapi.csr.course.CourseService;
import com.elonewong.onlinecourseapi.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CategoryService {

    private CategoryRepository categoryRepository;
    private CategoryResponseMapper categoryResponseMapper;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository,
                           CategoryResponseMapper categoryResponseMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryResponseMapper = categoryResponseMapper;
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream().map(categoryResponseMapper).collect(Collectors.toList());
    }

    public CategoryResponse getOneCategory(String categoryId) {
        // why the mapper can accept optional?
        // return categoryRepo.findById(categoryId).map(categoryResponseMapper).orElseThrow();
        Category category = this.findCategoryById(categoryId);
        return categoryResponseMapper.apply(category);
    }

//    public CategoryResponse addOneCategory(CategoryRequest categoryRequest) {
//        Category category = Category.builder()
//                .name(categoryRequest.name())
//                .courses(courseService.findCoursesByIds(categoryRequest.courseIds()))
//                .build();
//        return categoryResponseMapper.apply(categoryRepository.save(category));
//    }
//
//    public CategoryResponse updateOneCategory(String categoryId, CategoryRequest categoryRequest) {
//        Category category = this.findCategoryById(categoryId);
//        category.setName(categoryRequest.name());
//        category.setCourses(courseService.findCoursesByIds(categoryRequest.courseIds()));
//        return categoryResponseMapper.apply(categoryRepository.save(category));
//    }
//
//    public void deleteOneCategory(String categoryId) {
//        categoryRepository.delete(this.findCategoryById(categoryId));
//    }

    public Category findCategoryById(String categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category id not found"));
    }

    public List<Category> findCategoriesByIds(List<String> categoryIds) {
        List<Category> categories = categoryRepository.findAllCategoriesByIds(categoryIds);
        if (categories.size() == categoryIds.size()) {
            return categories;
        } else throw new ResourceNotFoundException("Some courses ids not found");
    }

}
