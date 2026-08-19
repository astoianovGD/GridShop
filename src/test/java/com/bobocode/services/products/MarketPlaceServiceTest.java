package com.bobocode.services.products;

import com.bobocode.dto.products.ProductCreateDto;
import com.bobocode.dto.products.ProductDto;
import com.bobocode.dto.users.UserDto;
import com.bobocode.entities.products.Category;
import com.bobocode.entities.products.Product;
import com.bobocode.entities.users.User;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.mappers.products.ProductCreateMapper;
import com.bobocode.mappers.products.ProductMapper;
import com.bobocode.mappers.users.UserMapper;
import com.bobocode.repositories.bucket.BucketItemRepository;
import com.bobocode.repositories.products.CategoryRepository;
import com.bobocode.repositories.products.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MarketPlaceServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private ProductMapper productMapper;
    @Mock private ProductCreateMapper productCreateMapper;
    @Mock private CategoryRepository categoryRepository;
    @Mock private BucketItemRepository bucketItemRepository;
    @Mock private UserMapper userMapper;

    @InjectMocks
    private MarketPlaceService marketPlaceService;

    @Test
    void shouldAddNewProductSuccessfully() {
        ProductCreateDto createDto = new ProductCreateDto();
        createDto.setCategoryId(1L);

        Product product = new Product();
        Category category = new Category();
        category.setId(1L);

        when(productCreateMapper.toEntity(createDto)).thenReturn(product);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        marketPlaceService.addNewProduct(createDto);

        assertEquals(category, product.getCategory());
        verify(productRepository).save(product);
    }

    @Test
    void shouldThrowEntityNotFoundWhenCategoryNotFoundOnAddNewProduct() {
        ProductCreateDto createDto = new ProductCreateDto();
        createDto.setCategoryId(99L);

        Product product = new Product();
        when(productCreateMapper.toEntity(createDto)).thenReturn(product);
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> marketPlaceService.addNewProduct(createDto));
        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldRemoveProductSuccessfully() {
        Product product = new Product();
        product.setId(1L);
        product.setActive(true);

        User user = new User();
        UserDto userDto = new UserDto();

        when(productRepository.findProductByIsActiveAndId(true, 1L)).thenReturn(Optional.of(product));
        when(bucketItemRepository.findActiveUsersByActiveProductIdInBucket(1L)).thenReturn(List.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        List<UserDto> affectedUsers = marketPlaceService.removeProduct(1L);

        assertEquals(1, affectedUsers.size());
        assertFalse(product.isActive());
        verify(bucketItemRepository).deleteAllByProductId(1L);
        verify(productRepository).save(product);
    }

    @Test
    void shouldThrowEntityNotFoundWhenProductNotFoundOnRemoval() {
        when(productRepository.findProductByIsActiveAndId(true, 99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> marketPlaceService.removeProduct(99L));
        verify(bucketItemRepository, never()).deleteAllByProductId(anyLong());
    }

    @Test
    void shouldEditProductSuccessfully() {
        ProductDto dto = new ProductDto();
        dto.setId(1L);
        dto.setName("Updated Name");
        dto.setPrice(new BigDecimal("100.00"));
        dto.setCategoryName("Electronics");

        Product existingProduct = new Product();
        existingProduct.setId(1L);
        Category category = new Category();
        category.setName("Electronics");

        when(productRepository.findProductByIsActiveAndId(true, 1L)).thenReturn(Optional.of(existingProduct));
        when(categoryRepository.findByName("Electronics")).thenReturn(Optional.of(category));

        marketPlaceService.editProduct(dto.getId(), dto);

        assertEquals("Updated Name", existingProduct.getName());
        assertEquals(0, existingProduct.getPrice().compareTo(new BigDecimal("100.00")));
        assertEquals(category, existingProduct.getCategory());
        verify(productRepository).save(existingProduct);
    }

    @Test
    void shouldThrowEntityNotFoundWhenProductNotFoundOnEdit() {
        ProductDto dto = new ProductDto();
        dto.setId(99L);

        when(productRepository.findProductByIsActiveAndId(true, 99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> marketPlaceService.editProduct(dto.getId(), dto));
        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldThrowEntityNotFoundWhenCategoryNotFoundOnEdit() {
        ProductDto dto = new ProductDto();
        dto.setId(1L);
        dto.setCategoryName("Unknown");

        Product existingProduct = new Product();
        when(productRepository.findProductByIsActiveAndId(true, 1L)).thenReturn(Optional.of(existingProduct));
        when(categoryRepository.findByName("Unknown")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> marketPlaceService.editProduct(dto.getId(), dto));
        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldGetAllProductsSuccessfully() {
        Product product = new Product();
        ProductDto dto = new ProductDto();

        when(productRepository.findAllByIsActive(true)).thenReturn(List.of(product));
        when(productMapper.toDto(product)).thenReturn(dto);

        List<ProductDto> result = marketPlaceService.getAllProducts();

        assertEquals(1, result.size());
        verify(productRepository).findAllByIsActive(true);
    }

    @Test
    void shouldReturnEmptyListWhenNoProductsActive() {
        when(productRepository.findAllByIsActive(true)).thenReturn(Collections.emptyList());

        List<ProductDto> result = marketPlaceService.getAllProducts();

        assertTrue(result.isEmpty());
        verifyNoInteractions(productMapper);
    }

    @Test
    void shouldGetProductByIdSuccessfully() {
        Product product = new Product();
        product.setId(5L);
        ProductDto dto = new ProductDto();

        when(productRepository.findProductByIsActiveAndId(true, 5L)).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(dto);

        ProductDto result = marketPlaceService.getProductById(5L);

        assertNotNull(result);
        verify(productRepository).findProductByIsActiveAndId(true, 5L);
    }

    @Test
    void shouldThrowEntityNotFoundWhenProductNotFoundById() {
        when(productRepository.findProductByIsActiveAndId(true, 99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> marketPlaceService.getProductById(99L));
    }

    @Test
    void shouldUpdateProductFieldSuccessfully() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Old Name");

        when(productRepository.findProductByIsActiveAndId(true, 1L)).thenReturn(Optional.of(product));

        marketPlaceService.updateProductField(1L, p -> p.setName("New Name"));

        assertEquals("New Name", product.getName());
        verify(productRepository).save(product);
    }

    @Test
    void shouldThrowEntityNotFoundWhenProductNotFoundOnFieldUpdate() {
        when(productRepository.findProductByIsActiveAndId(true, 99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> marketPlaceService.updateProductField(99L, p -> p.setName("Test")));
        verify(productRepository, never()).save(any());
    }
}