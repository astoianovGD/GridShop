package com.bobocode.menus.users;

import com.bobocode.dto.products.ProductDto;
import com.bobocode.dto.users.UserDto;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.menus.products.BucketMenu;
import com.bobocode.menus.products.CatalogMenu;
import com.bobocode.services.bucket.BucketService;
import com.bobocode.services.orders.OrderConsoleViewService;
import com.bobocode.services.orders.OrderService;
import com.bobocode.services.products.MarketPlaceService;
import com.bobocode.services.products.ProductsConsoleViewService;
import com.bobocode.services.user.UserConsoleViewService;
import com.bobocode.services.user.UserService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserMenuTest {

    @Mock private UserService userService;
    @Mock private BucketService bucketService;
    @Mock private MarketPlaceService marketPlaceService;
    @Mock private CatalogMenu catalogMenu;
    @Mock private UserConsoleViewService userConsoleViewService;
    @Mock private BucketMenu bucketMenu;
    @Mock private OrderService orderService;
    @Mock private OrderConsoleViewService orderConsoleViewService;
    @Mock private ProductsConsoleViewService productsConsoleViewService;

    @InjectMocks
    private UserMenu userMenu;

    private UserDto testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserDto();
        testUser.setId(1L);
        testUser.setEmail("user@test.com");
        testUser.setFirstname("Alex");
        testUser.setLastname("Stoianov");
    }

    @Test
    void shouldSignOutSuccessfully() {
        Scanner scanner = createScanner("0\n");

        userMenu.menu(testUser, scanner);

        verifyNoInteractions(userService, bucketService, marketPlaceService);
    }

    @Test
    void shouldHandleInvalidMenuOptionThenSignOut() {
        Scanner scanner = createScanner("invalid\n0\n");

        userMenu.menu(testUser, scanner);

        verifyNoInteractions(userService, bucketService, marketPlaceService);
    }

    @Test
    void shouldDeleteAccountWhenConfirmed() {
        Scanner scanner = createScanner("4\nY\n");

        userMenu.menu(testUser, scanner);

        verify(userService).deleteUserAccount(1L);
    }

    @Test
    void shouldNotDeleteAccountWhenNotConfirmed() {
        Scanner scanner = createScanner("4\nN\n0\n");

        userMenu.menu(testUser, scanner);

        verify(userService, never()).deleteUserAccount(anyLong());
    }

    @Test
    void shouldOpenBucketMenu() {
        Scanner scanner = createScanner("3\n0\n");

        userMenu.menu(testUser, scanner);

        verify(bucketMenu).handleBucket(eq(testUser), any(Scanner.class));
    }

    @Test
    void shouldBrowseProductsAndAddValidQuantity() {
        ProductDto product = new ProductDto();
        product.setId(10L);
        product.setName("Laptop");
        when(marketPlaceService.getAllProducts()).thenReturn(List.of(product));
        when(marketPlaceService.getProductById(10L)).thenReturn(product);

        // Input:
        // 1 (Browse Products)
        // 4 (Add to Bucket)
        // 10 (Product ID)
        // 2 (Quantity)
        // 0 (Nothing / Exit browse)
        // 0 (Sign Out)
        Scanner scanner = createScanner("1\n4\n10\n2\n0\n0\n");

        userMenu.menu(testUser, scanner);

        verify(bucketService).addProductToBucket(1L, 10L, 2);
    }

    @Test
    void shouldHandleInvalidQuantityWhenAddingToBucket() {
        ProductDto product = new ProductDto();
        product.setId(10L);
        when(marketPlaceService.getAllProducts()).thenReturn(List.of(product));

        // Input:
        // 1 (Browse)
        // 4 (Add to Bucket)
        // 10 (Product ID)
        // -1 (Invalid Quantity)
        // 0 (Nothing / Exit)
        // 0 (Sign Out)
        Scanner scanner = createScanner("1\n4\n10\n-1\n0\n0\n");

        userMenu.menu(testUser, scanner);

        verify(bucketService, never()).addProductToBucket(anyLong(), anyLong(), anyInt());
    }

    @Test
    void shouldHandleEntityNotFoundWhenAddingToBucket() {
        ProductDto product = new ProductDto();
        when(marketPlaceService.getAllProducts()).thenReturn(List.of(product));
        doThrow(new EntityNotFoundException("Product not found")).when(marketPlaceService).getProductById(99L);

        // Input:
        // 1 (Browse)
        // 4 (Add to Bucket)
        // 99 (Product ID)
        // 1 (Quantity)
        // 0 (Nothing)
        // 0 (Sign Out)
        Scanner scanner = createScanner("1\n4\n99\n1\n0\n0\n");

        userMenu.menu(testUser, scanner);

        verify(marketPlaceService).getProductById(99L);
        verify(bucketService, never()).addProductToBucket(anyLong(), anyLong(), anyInt());
    }

    @Test
    void shouldViewPersonalDataAndSeePurchaseHistory() {
        when(orderService.getUserOrders(1L)).thenReturn(Collections.emptyList());

        // Input:
        // 2 (View Personal Data)
        // 2 (See Purchase History)
        // 0 (Nothing)
        // 0 (Sign Out)
        Scanner scanner = createScanner("2\n2\n0\n0\n");

        userMenu.menu(testUser, scanner);

        verify(orderService).getUserOrders(1L);
        verify(orderConsoleViewService).displayOrderHistory(anyList());
    }

    @Test
    void shouldEditUserFirstNameSuccessfully() {
        // Input:
        // 2 (View Personal Data)
        // 1 (Edit profile)
        // 1 (First Name option)
        // "David" (New First Name)
        // 0 (Nothing)
        // 0 (Sign Out)
        Scanner scanner = createScanner("2\n1\n1\nDavid\n0\n0\n");

        userMenu.menu(testUser, scanner);

        verify(userService).updateUserField(eq(1L), any());
    }

    @Test
    void shouldEditUserAgeSuccessfully() {
        // Input:
        // 2 (View Personal Data)
        // 1 (Edit profile)
        // 3 (Age option)
        // 25 (New Age)
        // 0 (Nothing)
        // 0 (Sign Out)
        Scanner scanner = createScanner("2\n1\n3\n25\n0\n0\n");

        userMenu.menu(testUser, scanner);

        verify(userService).updateUserField(eq(1L), any());
    }

    @Test
    void shouldCancelProfileEdit() {
        // Input:
        // 2 (View Personal Data)
        // 1 (Edit profile)
        // 0 (Cancel)
        // 0 (Nothing)
        // 0 (Sign Out)
        Scanner scanner = createScanner("2\n1\n0\n0\n0\n");

        userMenu.menu(testUser, scanner);

        verify(userService, never()).updateUserField(anyLong(), any());
    }

    private Scanner createScanner(String input) {
        return new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
    }
}