package com.bobocode.services.bucket;

import com.bobocode.clients.product.ProductClient;
import com.bobocode.dto.bucket.BucketItemDto;
import com.bobocode.dto.products.ProductDto;
import com.bobocode.entities.bucket.Bucket;
import com.bobocode.entities.bucket.BucketItem;
import com.bobocode.mappers.bucket.BucketItemMapper;
import com.bobocode.repositories.bucket.BucketItemRepository;
import com.bobocode.repositories.bucket.BucketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BucketServiceTest {

    @Mock
    private BucketRepository bucketRepository;

    @Mock
    private BucketItemRepository bucketItemRepository;

    @Mock
    private BucketItemMapper bucketItemMapper;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private BucketService bucketService;

    private Bucket testBucket;
    private ProductDto testProductDto;

    @BeforeEach
    void setUp() {
        testBucket = new Bucket();
        testBucket.setId(10L);
        testBucket.setUserId(1L);
        testBucket.setItems(new ArrayList<>());

        testProductDto = new ProductDto();
        testProductDto.setId(5L);
        testProductDto.setName("Keyboard");
        testProductDto.setPrice(BigDecimal.valueOf(150.00));
        testProductDto.setCategoryName("Electronics");
    }

    @Test
    @DisplayName("addProductToBucket should validate product via Feign and add new item to bucket")
    void shouldAddNewProductToBucketSuccessfully() {
        when(productClient.getProductById(5L)).thenReturn(testProductDto);
        when(bucketRepository.findByUserId(1L)).thenReturn(Optional.of(testBucket));
        when(bucketItemRepository.findByBucketIdAndProductId(10L, 5L)).thenReturn(Optional.empty());
        when(bucketItemRepository.save(any(BucketItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        bucketService.addProductToBucket(1L, 5L, 2);

        verify(productClient).getProductById(5L);
        verify(bucketItemRepository).save(argThat(item ->
                item.getProductId().equals(5L) && item.getQuantity().equals(2)
        ));
    }

    @Test
    @DisplayName("addProductToBucket should increase quantity when item already exists in bucket")
    void shouldIncreaseQuantityWhenProductAlreadyInBucket() {
        BucketItem existingItem = new BucketItem();
        existingItem.setBucketItemId(100L);
        existingItem.setBucket(testBucket);
        existingItem.setProductId(5L);
        existingItem.setQuantity(3);

        when(productClient.getProductById(5L)).thenReturn(testProductDto);
        when(bucketRepository.findByUserId(1L)).thenReturn(Optional.of(testBucket));
        when(bucketItemRepository.findByBucketIdAndProductId(10L, 5L)).thenReturn(Optional.of(existingItem));
        when(bucketItemRepository.save(any(BucketItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        bucketService.addProductToBucket(1L, 5L, 2);

        verify(productClient).getProductById(5L);
        assertEquals(5, existingItem.getQuantity());
        verify(bucketItemRepository).save(existingItem);
    }

    @Test
    @DisplayName("removeProductFromBucket should remove item when bucket exists")
    void shouldRemoveProductFromBucket() {
        when(bucketRepository.findByUserId(1L)).thenReturn(Optional.of(testBucket));

        bucketService.removeProductFromBucket(1L, 5L);

        verify(bucketItemRepository).deleteByBucketIdAndProductId(10L, 5L);
    }

    @Test
    @DisplayName("removeProductFromBucket should do nothing when bucket does not exist")
    void shouldDoNothingOnRemoveWhenBucketDoesNotExist() {
        when(bucketRepository.findByUserId(1L)).thenReturn(Optional.empty());

        bucketService.removeProductFromBucket(1L, 5L);

        verify(bucketItemRepository, never()).deleteByBucketIdAndProductId(any(), any());
    }

    @Test
    @DisplayName("getProductsFromBucket should fetch product details via Feign and map items to DTOs")
    void shouldGetProductsFromBucketWithDetails() {
        BucketItem item = new BucketItem();
        item.setBucketItemId(1L);
        item.setProductId(5L);
        item.setQuantity(2);
        testBucket.getItems().add(item);

        BucketItemDto dto = new BucketItemDto();
        dto.setProductId(5L);
        dto.setName("Keyboard");
        dto.setPrice(BigDecimal.valueOf(150.00));
        dto.setQuantity(2);

        when(bucketRepository.findByUserId(1L)).thenReturn(Optional.of(testBucket));
        when(productClient.getProductById(5L)).thenReturn(testProductDto);
        when(bucketItemMapper.toDto(item, testProductDto)).thenReturn(dto);

        List<BucketItemDto> result = bucketService.getProductsFromBucket(1L);

        assertEquals(1, result.size());
        assertEquals("Keyboard", result.get(0).getName());
        verify(productClient).getProductById(5L);
        verify(bucketItemMapper).toDto(item, testProductDto);
    }

    @Test
    @DisplayName("clearBucket should delete all items by bucket ID")
    void shouldClearBucket() {
        when(bucketRepository.findByUserId(1L)).thenReturn(Optional.of(testBucket));

        bucketService.clearBucket(1L);

        verify(bucketItemRepository).deleteAllByBucketId(10L);
    }
}
