package com.bobocode.mappers.products;

import com.bobocode.dto.products.ProductDto;
import com.bobocode.entities.products.Category;
import com.bobocode.entities.products.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class ProductMapperTest {

    @Autowired
    private ProductMapper productMapper;

    @Test
    void shouldMapProductToDto() {
        // Arrange
        Category category = new Category();
        category.setName("Audio");

        Product product = new Product();
        product.setId(15L);
        product.setName("Bluetooth Speaker");
        product.setPrice(new BigDecimal("89.99"));
        product.setCategory(category);
        product.setActive(true);

        // Act
        ProductDto dto = productMapper.toDto(product);

        // Assert
        assertNotNull(dto);
        assertEquals(15L, dto.getId());
        assertEquals("Bluetooth Speaker", dto.getName());
        assertEquals(0, dto.getPrice().compareTo(new BigDecimal("89.99")));
        assertEquals("Audio", dto.getCategoryName());
    }

    @Test
    void shouldMapProductDtoToEntity() {
        // Arrange
        ProductDto dto = new ProductDto();
        dto.setId(20L); // ignored in mapper
        dto.setName("Smartwatch");
        dto.setPrice(new BigDecimal("249.99"));
        dto.setCategoryName("Wearables"); // ignored in mapper

        // Act
        Product product = productMapper.toEntity(dto);

        // Assert
        assertNotNull(product);
        assertEquals(0L, product.getId()); // ignored
        assertEquals("Smartwatch", product.getName());
        assertEquals(0, product.getPrice().compareTo(new BigDecimal("249.99")));
        assertNull(product.getCategory()); // ignored
        assertTrue(product.isActive()); // constant = "true"
    }
}