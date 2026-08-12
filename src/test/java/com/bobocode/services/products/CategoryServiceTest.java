package com.bobocode.services.products;

import com.bobocode.dto.products.CategoryCreateDto;
import com.bobocode.dto.products.CategoryDto;
import com.bobocode.entities.products.Category;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.mappers.products.CategoryCreateMapper;
import com.bobocode.mappers.products.CategoryMapper;
import com.bobocode.repositories.products.CategoryRepository;
import com.bobocode.repositories.products.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock private CategoryRepository categoryRepository;
    @Mock private CategoryCreateMapper categoryCreateMapper;
    @Mock private ProductRepository productRepository;
    @Mock private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void shouldAddNewCategorySuccessfully() {
        CategoryCreateDto createDto = new CategoryCreateDto();
        createDto.setName("Electronics");
        Category category = new Category();

        when(categoryRepository.existsByName("Electronics")).thenReturn(false);
        when(categoryCreateMapper.toEntity(createDto)).thenReturn(category);

        categoryService.addNewCategory(createDto);

        verify(categoryRepository).save(category);
    }

    @Test
    void shouldThrowIllegalArgumentWhenCategoryNameAlreadyExistsOnAdd() {
        CategoryCreateDto createDto = new CategoryCreateDto();
        createDto.setName("Electronics");

        when(categoryRepository.existsByName("Electronics")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> categoryService.addNewCategory(createDto));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void shouldEditCategorySuccessfullyWhenNameChanged() {
        Category category = new Category();
        category.setId(1L);
        category.setName("OldName");

        CategoryDto dto = new CategoryDto();
        dto.setName("NewName");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName("NewName")).thenReturn(false);

        categoryService.editCategory(dto, 1L);

        assertEquals("NewName", category.getName());
        verify(categoryRepository).save(category);
    }

    @Test
    void shouldEditCategorySuccessfullyWhenNameUnchanged() {
        Category category = new Category();
        category.setId(1L);
        category.setName("SameName");

        CategoryDto dto = new CategoryDto();
        dto.setName("SameName");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.editCategory(dto, 1L);

        assertEquals("SameName", category.getName());
        verify(categoryRepository).save(category);
        verify(categoryRepository, never()).existsByName(any());
    }

    @Test
    void shouldThrowEntityNotFoundWhenCategoryNotFoundOnEdit() {
        CategoryDto dto = new CategoryDto();
        dto.setName("Name");

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.editCategory(dto, 99L));
    }

    @Test
    void shouldThrowIllegalArgumentWhenNewCategoryNameAlreadyExistsOnEdit() {
        Category category = new Category();
        category.setId(1L);
        category.setName("OldName");

        CategoryDto dto = new CategoryDto();
        dto.setName("ExistingName");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName("ExistingName")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> categoryService.editCategory(dto, 1L));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void shouldRemoveCategorySuccessfully() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(productRepository.existsByCategoryId(1L)).thenReturn(false);

        categoryService.removeCategory(1L);

        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void shouldThrowEntityNotFoundWhenCategoryNotFoundOnRemoval() {
        when(categoryRepository.existsById(99L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> categoryService.removeCategory(99L));
        verify(categoryRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldThrowIllegalStateWhenCategoryContainsProductsOnRemoval() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(productRepository.existsByCategoryId(1L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> categoryService.removeCategory(1L));
        verify(categoryRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldGetAllCategoriesSuccessfully() {
        Category category = new Category();
        CategoryDto dto = new CategoryDto();

        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(categoryMapper.toDto(category)).thenReturn(dto);

        List<CategoryDto> result = categoryService.getAllCategories();

        assertEquals(1, result.size());
        verify(categoryRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoCategoriesFound() {
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

        List<CategoryDto> result = categoryService.getAllCategories();

        assertTrue(result.isEmpty());
        verifyNoInteractions(categoryMapper);
    }

    @Test
    void shouldCheckCategoryExistsById() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(categoryRepository.existsById(2L)).thenReturn(false);

        assertTrue(categoryService.isCategoryExists(1L));
        assertFalse(categoryService.isCategoryExists(2L));
    }

    @Test
    void shouldGetCategoryEntityByIdSuccessfully() {
        Category category = new Category();
        category.setId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Category result = categoryService.getCategoryEntityById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void shouldThrowEntityNotFoundWhenCategoryEntityNotFoundById() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.getCategoryEntityById(99L));
    }
}