package com.bobocode.mappers.bucket;

import com.bobocode.dto.bucket.BucketItemDto;
import com.bobocode.entities.bucket.BucketItem;
import com.bobocode.entities.products.Category;
import com.bobocode.entities.products.Product;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class BucketItemMapperTest {

    private final BucketItemMapper mapper = Mappers.getMapper(BucketItemMapper.class);

    @Test
    void shouldMapBucketItemToDto() {
        // Arrange
        Category category = new Category();
        category.setName("Electronics");

        Product product = new Product();
        product.setId(42L);
        product.setName("Wireless Mouse");
        product.setPrice(new BigDecimal("25.50"));
        product.setCategory(category);

        BucketItem bucketItem = new BucketItem();
        bucketItem.setBucketItemId(1L);
        bucketItem.setQuantity(3);
        bucketItem.setProduct(product);

        // Act
        BucketItemDto dto = mapper.toDto(bucketItem);

        // Assert
        assertNotNull(dto);
        assertEquals(42L, dto.getProductId());
        assertEquals("Wireless Mouse", dto.getName());
        assertEquals(0, dto.getPrice().compareTo(new BigDecimal("25.50")));
        assertEquals("Electronics", dto.getCategoryName());
        assertEquals(3, dto.getQuantity());
    }
}