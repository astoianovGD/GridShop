package com.bobocode.Menus;

import com.bobocode.Entities.Products.Product;
import com.bobocode.Entities.Users.User;
import com.bobocode.Enums.Gender;
import com.bobocode.Exceptions.EntityNotFoundException;
import com.bobocode.Services.Products.BucketService;
import com.bobocode.Services.Products.MarketPlaceService;
import com.bobocode.Services.Products.OrderService;
import com.bobocode.Services.User.UserConsoleViewService;
import com.bobocode.Services.User.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserMenuTest {

    private UserService userServiceMock;
    private BucketService bucketServiceMock;
    private MarketPlaceService marketPlaceServiceMock;
    private CatalogMenu catalogMenuMock;
    private UserConsoleViewService userConsoleViewServiceMock;
    private BucketMenu bucketMenuMock;
    private OrderService orderServiceMock;
    private UserMenu userMenu;

    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        userServiceMock = mock(UserService.class);
        bucketServiceMock = mock(BucketService.class);
        marketPlaceServiceMock = mock(MarketPlaceService.class);
        catalogMenuMock = mock(CatalogMenu.class);
        userConsoleViewServiceMock = mock(UserConsoleViewService.class);
        bucketMenuMock = mock(BucketMenu.class);
        orderServiceMock = mock(OrderService.class);

        userMenu = new UserMenu(userServiceMock, bucketServiceMock, marketPlaceServiceMock,
                catalogMenuMock, userConsoleViewServiceMock, bucketMenuMock, orderServiceMock);

        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Should handle invalid option in main user menu then sign out")
    void testMenu_InvalidOptionThenSignOut() {
        User user = new User();
        Scanner scanner = new Scanner("99\n0\n");
        userMenu.menu(user, scanner);
        assertTrue(outContent.toString().contains("Invalid option!"));
    }

    @Test
    @DisplayName("Should successfully add product to bucket with amount specified")
    void testHandleBrowseProducts_AddToBucket_Success() {
        User user = new User();
        user.setId(10L);
        Product product = new Product(5L, "Phone", BigDecimal.valueOf(500), 1L, 10);

        when(marketPlaceServiceMock.getAllProducts()).thenReturn(List.of(product));
        when(marketPlaceServiceMock.getProductById(5L)).thenReturn(product);

        // Flow: 1 (Browse) -> 4 (Add to Bucket) -> "5" (Product ID) -> "2" (Amount) -> 0 (Exit browse) -> 0 (Sign out)
        Scanner scanner = new Scanner("1\n4\n5\n2\n0\n0\n");

        userMenu.menu(user, scanner);

        verify(bucketServiceMock, times(1)).addProductToBucket(10L, 5L, 2);
        assertTrue(outContent.toString().contains("2x Phone added to bucket!"));
    }

    @Test
    @DisplayName("Should handle NumberFormatException when adding product to bucket with invalid quantity")
    void testHandleBrowseProducts_AddToBucket_InvalidAmount() {
        User user = new User();
        user.setId(1L);
        when(marketPlaceServiceMock.getAllProducts()).thenReturn(Collections.emptyList());

        // Flow: 1 -> 4 -> "5" (ID) -> "abc" (invalid amount) -> 0 -> 0
        Scanner scanner = new Scanner("1\n4\n5\nabc\n0\n0\n");

        userMenu.menu(user, scanner);

        assertTrue(outContent.toString().contains("Invalid quantity! Must be a positive number."));
        verify(bucketServiceMock, never()).addProductToBucket(anyLong(), anyLong(), anyInt());
    }

    @Test
    @DisplayName("Should handle view personal data menu options and fetch history from OrderService")
    void testHandleViewPersonalData_HistoryAndExit() {
        User user = new User();
        user.setId(1L);
        when(orderServiceMock.getOrderHistory(1L)).thenReturn(List.of("Order ID: 1 | Date: ..."));

        // Flow: 2 (View Personal Data) -> 2 (See Purchase History) -> 0 (Nothing) -> 0 (Sign out)
        Scanner scanner = new Scanner("2\n2\n0\n0\n");
        userMenu.menu(user, scanner);

        verify(userConsoleViewServiceMock, times(1)).printUserProfile(user);
        verify(orderServiceMock, times(1)).getOrderHistory(1L);
        assertTrue(outContent.toString().contains("Order ID: 1 | Date: ..."));
    }
}