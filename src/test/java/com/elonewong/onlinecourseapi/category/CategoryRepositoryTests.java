package com.elonewong.onlinecourseapi.category;

import com.elonewong.onlinecourseapi.csr.category.Category;
import com.elonewong.onlinecourseapi.csr.category.CategoryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test; // import this instead of org.junit.Test for spring boot version 2.2 or onwards
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CategoryRepositoryTests {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void test_CategoryRepository_FindAllCategoriesByIds_ReturnListHaveCorrectSizeAndCorrectElements() {

        // Arrange
        List<Category> categories = new ArrayList<>();
        for (int i = 0; i < 3; i ++) {
            categories.add(Category.builder()
                    .courses(Collections.emptyList())
                    .name("Category " + i)
                    .build());
        }
        categoryRepository.saveAll(categories);

        // Act
        List<Category> allCategoriesByIds = categoryRepository.findAllCategoriesByIds(
                List.of(categories.get(0).getId(), categories.get(1).getId())
        );

        // Assert
        Assertions.assertThat(allCategoriesByIds.size() == 2).isTrue();
        Assertions.assertThat(allCategoriesByIds.containsAll(
                List.of(categories.get(0), categories.get(1))
        )).isTrue();

    }

}
