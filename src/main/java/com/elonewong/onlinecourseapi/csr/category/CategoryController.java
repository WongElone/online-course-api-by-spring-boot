package com.elonewong.onlinecourseapi.csr.category;

import com.elonewong.onlinecourseapi.advice.ApplicationExceptionHandler.CommonErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Get existing course categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of categories found"),
    })
    @GetMapping
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @Operation(summary = "Get a course category by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the category"),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(schema = @Schema(implementation = CommonErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public CategoryResponse getOneCategory(@PathVariable String id) {
        return categoryService.getOneCategory(id);
    }

//    @PostMapping
//    @ResponseStatus(value = HttpStatus.CREATED)
//    public CategoryResponse addOneCategory(@RequestBody @Valid CategoryRequest categoryRequest) {
//        return categoryService.addOneCategory(categoryRequest);
//    }
//
//    @PutMapping("/{id}")
//    public CategoryResponse updateOneCategory(@PathVariable String id, @RequestBody @Valid CategoryRequest categoryRequest) {
//        return categoryService.updateOneCategory(id, categoryRequest);
//    }
//
//    @DeleteMapping("/{id}")
//    @ResponseStatus(value = HttpStatus.NO_CONTENT)
//    public void deleteOneCategory(@PathVariable String id) {
//        categoryService.deleteOneCategory(id);
//    }
}
