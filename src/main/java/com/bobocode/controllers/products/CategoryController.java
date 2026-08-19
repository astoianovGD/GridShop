package com.bobocode.controllers.products;

import com.bobocode.dto.products.CategoryCreateDto;
import com.bobocode.dto.products.CategoryDto;
import com.bobocode.services.products.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Retrieves all product categories.
     * GET /api/v1/categories
     *
     * @return a list of category DTOs
     */
    @GetMapping
    public List<CategoryDto> getAllCategories() {
        return categoryService.getAllCategories();
    }

    /**
     * Retrieves a specific category by its ID.
     * GET /api/v1/categories/{id}
     *
     * @param id the ID of the category to retrieve
     * @return the matching category DTO
     */
    @GetMapping("/{id}")
    public CategoryDto getCategoryById(@PathVariable long id) {
        return categoryService.getCategoryDtoById(id);
    }

    /**
     * Creates a new product category.
     * POST /api/v1/categories
     *
     * @param categoryCreateDto the payload containing category creation details
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addNewCategory(@RequestBody CategoryCreateDto categoryCreateDto) {
        categoryService.addNewCategory(categoryCreateDto);
    }

    /**
     * Updates an existing category by its ID.
     * PUT /api/v1/categories/{id}
     *
     * @param id the ID of the category to update
     * @param categoryDto the payload containing updated category details
     */
    @PutMapping("/{id}")
    public void editCategoryById(@PathVariable long id, @RequestBody CategoryDto categoryDto) {
        categoryService.editCategory(categoryDto, id);
    }

    /**
     * Deletes a category by its ID.
     * DELETE /api/v1/categories/{id}
     *
     * @param id the ID of the category to delete
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryById(@PathVariable long id) {
        categoryService.removeCategory(id);
    }
}