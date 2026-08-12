package com.bobocode.mappers.products;

import com.bobocode.dto.products.CategoryDto;
import com.bobocode.entities.products.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class CategoryMapperTest {

    @Autowired
    private CategoryMapper categoryMapper;

    @Test
    void shouldMapCategoryToDto() {
        // Arrange
        Category category = new Category();
        category.setId(5L);
        category.setName("Laptops");

        // Act
        CategoryDto dto = categoryMapper.toDto(category);

        // Assert
        assertNotNull(dto);
        assertEquals(5L, dto.getId());
        assertEquals("Laptops", dto.getName());
    }

    @Test
    void shouldMapCategoryDtoToEntity() {
        // Arrange
        CategoryDto dto = new CategoryDto();
        dto.setId(10L);
        dto.setName("Smartphones");

        // Act
        Category category = categoryMapper.toEntity(dto);

        // Assert
        assertNotNull(category);
        assertEquals(10L, category.getId());
        assertEquals("Smartphones", category.getName());
    }
}