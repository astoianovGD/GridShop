package com.bobocode.services.bucket;

import com.bobocode.dto.bucket.BucketItemDto;
import com.bobocode.entities.bucket.Bucket;
import com.bobocode.entities.bucket.BucketItem;
import com.bobocode.entities.products.Product;
import com.bobocode.entities.users.User;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.mappers.bucket.BucketItemMapper;
import com.bobocode.repositories.bucket.BucketItemRepository;
import com.bobocode.repositories.bucket.BucketRepository;
import com.bobocode.repositories.products.ProductRepository;
import com.bobocode.repositories.users.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BucketServiceTest {

    @Mock private BucketRepository bucketRepository;
    @Mock private BucketItemRepository bucketItemRepository;
    @Mock private ProductRepository productRepository;
    @Mock private UserRepository userRepository;
    @Mock private BucketItemMapper bucketItemMapper;

    @InjectMocks
    private BucketService bucketService;

    private User testUser;
    private Bucket testBucket;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setActive(true);

        testBucket = new Bucket();
        testBucket.setId(1L);
        testBucket.setUser(testUser);
        testBucket.setItems(new ArrayList<>());

        testProduct = new Product();
        testProduct.setId(10L);
        testProduct.setName("Laptop");
        testProduct.setActive(true);
    }

    @Test
    void shouldAddNewProductToExistingBucket() {
        when(bucketRepository.findByUserId(1L)).thenReturn(Optional.of(testBucket));
        when(productRepository.findProductByIsActiveAndId(true, 10L)).thenReturn(Optional.of(testProduct));
        when(bucketItemRepository.findByBucketIdAndProductId(testBucket.getId(), 10L)).thenReturn(Optional.empty());

        bucketService.addProductToBucket(1L, 10L, 2);

        verify(bucketItemRepository).save(any(BucketItem.class));
        assertEquals(1, testBucket.getItems().size());
        assertEquals(2, testBucket.getItems().get(0).getQuantity());
    }

    @Test
    void shouldIncreaseQuantityWhenProductAlreadyInBucket() {
        BucketItem existingItem = new BucketItem();
        existingItem.setBucket(testBucket);
        existingItem.setProduct(testProduct);
        existingItem.setQuantity(3);
        testBucket.getItems().add(existingItem);

        when(bucketRepository.findByUserId(1L)).thenReturn(Optional.of(testBucket));
        when(productRepository.findProductByIsActiveAndId(true, 10L)).thenReturn(Optional.of(testProduct));
        when(bucketItemRepository.findByBucketIdAndProductId(testBucket.getId(), 10L)).thenReturn(Optional.of(existingItem));

        bucketService.addProductToBucket(1L, 10L, 2);

        verify(bucketItemRepository).save(existingItem);
        assertEquals(5, existingItem.getQuantity());
    }

    @Test
    void shouldThrowEntityNotFoundWhenProductNotFoundOnAdd() {
        when(bucketRepository.findByUserId(1L)).thenReturn(Optional.of(testBucket));
        when(productRepository.findProductByIsActiveAndId(true, 99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bucketService.addProductToBucket(1L, 99L, 1));
        verify(bucketItemRepository, never()).save(any());
    }

    @Test
    void shouldCreateBucketAndAddProductWhenBucketDoesNotExist() {
        when(bucketRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(userRepository.findUserByIdAndRoleNameAndIsActive(1L, "USER", true)).thenReturn(Optional.of(testUser));
        when(bucketRepository.saveAndFlush(any(Bucket.class))).thenReturn(testBucket);
        when(productRepository.findProductByIsActiveAndId(true, 10L)).thenReturn(Optional.of(testProduct));
        when(bucketItemRepository.findByBucketIdAndProductId(testBucket.getId(), 10L)).thenReturn(Optional.empty());

        bucketService.addProductToBucket(1L, 10L, 1);

        verify(bucketRepository).saveAndFlush(any(Bucket.class));
        verify(bucketItemRepository).save(any(BucketItem.class));
    }

    @Test
    void shouldRemoveProductFromBucket() {
        when(bucketRepository.findByUserId(1L)).thenReturn(Optional.of(testBucket));

        bucketService.removeProductFromBucket(1L, 10L);

        verify(bucketItemRepository).deleteByBucketIdAndProductId(testBucket.getId(), 10L);
    }

    @Test
    void shouldDoNothingOnRemoveWhenBucketNotFound() {
        when(bucketRepository.findByUserId(1L)).thenReturn(Optional.empty());

        bucketService.removeProductFromBucket(1L, 10L);

        verify(bucketItemRepository, never()).deleteByBucketIdAndProductId(anyLong(), anyLong());
    }

    @Test
    void shouldGetProductsFromBucketSuccessfully() {
        BucketItem item = new BucketItem();
        item.setProduct(testProduct);
        item.setQuantity(1);
        testBucket.getItems().add(item);

        BucketItemDto itemDto = new BucketItemDto();
        itemDto.setProductId(10L);

        when(bucketRepository.findByUserId(1L)).thenReturn(Optional.of(testBucket));
        when(bucketItemMapper.toDto(item)).thenReturn(itemDto);

        List<BucketItemDto> result = bucketService.getProductsFromBucket(1L);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getProductId());
    }

    @Test
    void shouldReturnEmptyListWhenBucketNotFoundOnGet() {
        when(bucketRepository.findByUserId(1L)).thenReturn(Optional.empty());

        List<BucketItemDto> result = bucketService.getProductsFromBucket(1L);

        assertTrue(result.isEmpty());
        verifyNoInteractions(bucketItemMapper);
    }

    @Test
    void shouldClearBucketSuccessfully() {
        when(bucketRepository.findByUserId(1L)).thenReturn(Optional.of(testBucket));

        bucketService.clearBucket(1L);

        verify(bucketItemRepository).deleteAllByBucketId(testBucket.getId());
    }
}