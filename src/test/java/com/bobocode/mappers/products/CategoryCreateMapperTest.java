package com.bobocode.mappers.products;

import com.bobocode.dto.products.CategoryCreateDto;
import com.bobocode.entities.products.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class CategoryCreateMapperTest {

    @Autowired
    private CategoryCreateMapper categoryCreateMapper;

    @Test
    void shouldMapCategoryCreateDtoToEntity() {
        // Arrange
        CategoryCreateDto dto = new CategoryCreateDto();
        dto.setName("New Category");

        // Act
        Category category = categoryCreateMapper.toEntity(dto);

        // Assert
        assertNotNull(category);
        assertEquals(0L, category.getId()); // id should be ignored/default
        assertEquals("New Category", category.getName());
    }
}