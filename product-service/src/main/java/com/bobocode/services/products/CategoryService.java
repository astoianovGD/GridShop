package com.bobocode.services.products;

import com.bobocode.configs.RedisCacheConfig;
import com.bobocode.dto.products.CategoryCreateDto;
import com.bobocode.dto.products.CategoryDto;
import com.bobocode.entities.products.Category;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.mappers.products.CategoryCreateMapper;
import com.bobocode.mappers.products.CategoryMapper;
import com.bobocode.repositories.products.CategoryRepository;
import com.bobocode.repositories.products.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing categories.
 */
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CategoryService {

    /**
     * Repository for managing category entities.
     */
    private final CategoryRepository categoryRepository;

    /**
     * Mapper for category creation DTOs.
     */
    private final CategoryCreateMapper categoryCreateMapper;

    /**
     * Repository for managing product entities.
     */
    private final ProductRepository productRepository;

    /**
     * Mapper for category DTOs.
     */
    private final CategoryMapper categoryMapper;

    /**
     * Add new category and invalidate category caches in Redis.
     *
     * @param category the name of the category
     */
    @Transactional
    @CacheEvict(
            value = {RedisCacheConfig.CATEGORIES_CACHE, RedisCacheConfig.CATEGORY_BY_ID_CACHE},
            allEntries = true
    )
    public void addNewCategory(final CategoryCreateDto category) {
        if (isCategoryNameExists(category.getName())) {
            throw new IllegalArgumentException(
                    "Category with name '" + category.getName()
                            + "' already exists!"
            );
        }
        categoryRepository.save(categoryCreateMapper.toEntity(category));
    }

    /**
     * Changing existing category and invalidating category caches in Redis.
     *
     * @param categoryDto the new name of the category
     * @param id          the ID of the category to update
     */
    @Transactional
    @CacheEvict(
            value = {RedisCacheConfig.CATEGORIES_CACHE, RedisCacheConfig.CATEGORY_BY_ID_CACHE},
            allEntries = true
    )
    public void editCategory(final CategoryDto categoryDto, final Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Category with id " + id + " not found!"
                ));

        String newName = categoryDto.getName();
        if (!category.getName().equals(newName)
                && isCategoryNameExists(newName)) {
            throw new IllegalArgumentException(
                    "Category with name '" + newName + "' already exists!"
            );
        }

        category.setName(newName);

        categoryRepository.save(category);
    }

    /**
     * Removes a category by its ID if it doesn't contain any products,
     * and invalidates category caches in Redis.
     *
     * @param categoryId the ID of the category to remove
     * @throws EntityNotFoundException if the category is not found
     * @throws IllegalStateException   if the category contains products
     */
    @Transactional
    @CacheEvict(
            value = {RedisCacheConfig.CATEGORIES_CACHE, RedisCacheConfig.CATEGORY_BY_ID_CACHE},
            allEntries = true
    )
    public void removeCategory(final Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new EntityNotFoundException(
                    "Category with id " + categoryId + " not found!"
            );
        }

        if (productRepository.existsByCategoryId(categoryId)) {
            throw new IllegalStateException(
                    "Cannot delete category: it still contains products. "
                            + "Reassign or delete them first."
            );
        }

        categoryRepository.deleteById(categoryId);
    }

    /**
     * Helper method to check if category name is already taken.
     *
     * @param name the name of the category to check
     * @return true if the category name exists, false otherwise
     */
    private boolean isCategoryNameExists(final String name) {
        return categoryRepository.existsByName(name);
    }

    /**
     * Retrieves a list of all categories (cached in Redis).
     *
     * @return a list containing all categories
     */
    @Cacheable(value = RedisCacheConfig.CATEGORIES_CACHE)
    public List<CategoryDto> getAllCategories() {
        return categoryRepository
                .findAll()
                .stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    /**
     * Checks if a category exists by its ID.
     *
     * @param categoryId the ID to check
     * @return true if exists, false otherwise
     */
    public boolean isCategoryExists(final Long categoryId) {
        return categoryRepository.existsById(categoryId);
    }

    /**
     * Retrieves the category entity by its ID.
     *
     * @param id the category ID
     * @return the category entity
     */
    public Category getCategoryEntityById(final Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Category with ID " + id + " not found!"
                ));
    }

    @Cacheable(value = RedisCacheConfig.CATEGORY_BY_ID_CACHE, key = "#id")
    public CategoryDto getCategoryDtoById(final Long id) {
        Category category = getCategoryEntityById(id);
        return categoryMapper.toDto(category);
    }
}
