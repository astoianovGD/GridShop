package com.bobocode.mappers.products;

import com.bobocode.dto.products.ProductCreateDto;
import com.bobocode.entities.products.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class ProductCreateMapperTest {

    @Autowired
    private ProductCreateMapper productCreateMapper;

    @Test
    void shouldMapProductCreateDtoToEntity() {
        // Arrange
        ProductCreateDto dto = new ProductCreateDto();
        dto.setName("Wireless Headphones");
        dto.setPrice(new BigDecimal("199.99"));

        // Act
        Product product = productCreateMapper.toEntity(dto);

        // Assert
        assertNotNull(product);
        assertEquals(0L, product.getId()); // ignored
        assertEquals("Wireless Headphones", product.getName());
        assertEquals(0, product.getPrice().compareTo(new BigDecimal("199.99")));
        assertNull(product.getCategory()); // ignored
        assertTrue(product.isActive()); // constant = "true"
    }
}