package com.bobocode.menus.products;

import com.bobocode.dto.bucket.BucketItemDto;
import com.bobocode.dto.users.UserDto;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.services.bucket.BucketConsoleViewService;
import com.bobocode.services.bucket.BucketService;
import com.bobocode.services.orders.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BucketMenuTest {

    @Mock
    private BucketService bucketService;

    @Mock
    private OrderService orderService;

    @Mock
    private BucketConsoleViewService bucketConsoleViewService;

    @InjectMocks
    private BucketMenu bucketMenu;

    private UserDto testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserDto();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");
    }

    @Test
    void shouldReturnWhenBucketIsEmpty() {
        // Arrange
        when(bucketService.getProductsFromBucket(1L)).thenReturn(Collections.emptyList());
        Scanner scanner = new Scanner(System.in);

        // Act
        bucketMenu.handleBucket(testUser, scanner);

        // Assert
        verify(bucketService).getProductsFromBucket(1L);
        verifyNoInteractions(bucketConsoleViewService, orderService);
    }

    @Test
    void shouldHandleCheckoutSuccessfully() {
        // Arrange
        BucketItemDto itemDto = new BucketItemDto();
        when(bucketService.getProductsFromBucket(1L))
                .thenReturn(List.of(itemDto))
                .thenReturn(Collections.emptyList()); // after checkout bucket becomes empty or loop breaks

        // Input: "1" (purchase), "1234567812345678" (valid card)
        String simulatedInput = "1\n1234567812345678\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8)));

        // Act
        bucketMenu.handleBucket(testUser, scanner);

        // Assert
        verify(bucketConsoleViewService).displayBucket(anyList());
        verify(orderService).createOrderFromBucket(1L);
    }

    @Test
    void shouldRetryCardWhenFormatIsInvalidThenSucceed() {
        // Arrange
        BucketItemDto itemDto = new BucketItemDto();
        when(bucketService.getProductsFromBucket(1L)).thenReturn(List.of(itemDto));

        // Input: "1", "invalid-card", "1234-5678-1234-5678" (valid 16 digits after space removal)
        String simulatedInput = "1\ninvalid-card\n1234 5678 1234 5678\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8)));

        // Act
        bucketMenu.handleBucket(testUser, scanner);

        // Assert
        verify(orderService).createOrderFromBucket(1L);
    }

    @Test
    void shouldRemoveItemSuccessfully() {
        // Arrange
        BucketItemDto itemDto = new BucketItemDto();
        // Перший виклик повертає список (вхід у цикл), другий — порожній (вихід з циклю)
        when(bucketService.getProductsFromBucket(1L))
                .thenReturn(List.of(itemDto))
                .thenReturn(Collections.emptyList());

        // Input: "2" (remove), "10" (product id), "0" (go back / exit loop)
        String simulatedInput = "2\n10\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8)));

        // Act
        bucketMenu.handleBucket(testUser, scanner);

        // Assert
        verify(bucketService).removeProductFromBucket(1L, 10L);
    }

    @Test
    void shouldHandleEntityNotFoundExceptionDuringRemoval() {
        // Arrange
        BucketItemDto itemDto = new BucketItemDto();
        when(bucketService.getProductsFromBucket(1L))
                .thenReturn(List.of(itemDto))
                .thenReturn(Collections.emptyList());

        doThrow(new EntityNotFoundException("Product not found"))
                .when(bucketService).removeProductFromBucket(1L, 99L);

        // Input: "2", "99", "0"
        String simulatedInput = "2\n99\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8)));

        // Act
        bucketMenu.handleBucket(testUser, scanner);

        // Assert
        verify(bucketService).removeProductFromBucket(1L, 99L);
    }

    @Test
    void shouldHandleInvalidOptionAndGoBack() {
        // Arrange
        BucketItemDto itemDto = new BucketItemDto();
        when(bucketService.getProductsFromBucket(1L))
                .thenReturn(List.of(itemDto))
                .thenReturn(Collections.emptyList());

        // Input: "invalid_option", "0" (go back)
        String simulatedInput = "invalid_option\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8)));

        // Act
        bucketMenu.handleBucket(testUser, scanner);

        // Assert
        verify(bucketConsoleViewService).displayBucket(anyList());
    }
}