package com.elonewong.onlinecourseapi.category;

import com.elonewong.onlinecourseapi.csr.category.*;
import com.elonewong.onlinecourseapi.csr.course.Course;
import com.elonewong.onlinecourseapi.csr.course.CourseService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTests {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CourseService courseService;
    @Spy
    private CategoryResponseMapper categoryResponseMapper;
    @InjectMocks
    private CategoryService categoryService;

    private Category category1;
    private Course course1;

    @BeforeEach
    public void setup() {
        this.categoryResponseMapper = new CategoryResponseMapper();
        MockitoAnnotations.openMocks(this); // to inject the @InjectMocks fields

        course1 = Course.builder()
                .id("course-1")
                .title("Course 1")
                .build();
        category1 = Category.builder()
                .id("category-1")
                .name("Category 1")
                .courses(List.of(course1))
                .build();
        course1.setCategories(List.of(category1));
    }

    @Test
    public void test_CategoryService_GetAllCategories_ReturnsListOfCorrectSizeAndElements() {
        when(categoryRepository.findAll()).thenReturn(List.of(category1));

        List<CategoryResponse> categoryResponses = categoryService.getAllCategories();

        Assertions.assertThat(categoryResponses).hasSize(1);
        Assertions.assertThat(categoryResponses).containsAll(List.of(
            categoryResponseMapper.apply(category1)
        ));
    }

    @Test
    public void test_CategoryService_GetOneCategory_ReturnsCorrectCategoryResponse() {
        when(categoryRepository.findById("category-1")).thenReturn(Optional.ofNullable(category1));

        CategoryResponse categoryResponse = categoryService.getOneCategory("category-1");

        Assertions.assertThat(categoryResponse).isEqualTo(
        categoryResponseMapper.apply(category1)
        );
    }

    @Test
    public void test_CategoryService_FindCategoryById_ReturnsCorrectCategory() {
        when(categoryRepository.findById("category-1")).thenReturn(Optional.ofNullable(category1));

        Category category = categoryService.findCategoryById("category-1");

        Assertions.assertThat(category).isEqualTo(category1);
    }

    @Test
    public void test_CategoryService_FindCategoriesByIds_ReturnsListOfCorrectSizeAndElements() {
        when(categoryRepository.findAllCategoriesByIds(List.of("category-1")))
                .thenReturn(List.of(category1));

        List<Category> categories = categoryService.findCategoriesByIds(List.of("category-1"));

        Assertions.assertThat(categories).hasSize(1);
        Assertions.assertThat(categories).containsAll(List.of(category1));
    }
}
