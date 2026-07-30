package com.bobocode.Menus;

import com.bobocode.Entities.Products.Category;
import com.bobocode.Entities.Products.Product;
import com.bobocode.Entities.Users.User;
import com.bobocode.Exceptions.EntityNotFoundException;
import com.bobocode.Services.Products.BucketService;
import com.bobocode.Services.Products.CategoryService;
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

class StaffMenuTest {

    private UserService userServiceMock;
    private MarketPlaceService marketPlaceServiceMock;
    private CatalogMenu catalogMenuMock;
    private UserConsoleViewService userConsoleViewServiceMock;
    private BucketService bucketServiceMock;
    private OrderService orderServiceMock;
    private CategoryService categoryServiceMock;
    private StaffMenu staffMenu;

    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        userServiceMock = mock(UserService.class);
        marketPlaceServiceMock = mock(MarketPlaceService.class);
        catalogMenuMock = mock(CatalogMenu.class);
        userConsoleViewServiceMock = mock(UserConsoleViewService.class);
        bucketServiceMock = mock(BucketService.class);
        orderServiceMock = mock(OrderService.class);
        categoryServiceMock = mock(CategoryService.class);

        staffMenu = new StaffMenu(userServiceMock, marketPlaceServiceMock, catalogMenuMock,
                userConsoleViewServiceMock, bucketServiceMock, orderServiceMock, categoryServiceMock);

        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Should sign out when option 0 is selected in staff menu")
    void testMenu_SignOut() {
        Scanner scanner = new Scanner("0\n");
        staffMenu.menu(scanner);
        assertTrue(outContent.toString().contains("--- Staff Menu ---"));
    }

    @Test
    @DisplayName("Should successfully add a new product")
    void testHandleAddProduct_Success() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        when(marketPlaceServiceMock.getAllCategories()).thenReturn(List.of(category));
        when(marketPlaceServiceMock.isCategoryExists(1L)).thenReturn(true);

        // Flow: 1 (Add Product) -> 1 (Category ID) -> Name -> Price -> 0 (Sign out)
        Scanner scanner = new Scanner("1\n1\nKeyboard\n45.99\n0\n");
        staffMenu.menu(scanner);

        verify(marketPlaceServiceMock, times(1)).addNewProduct(any(Product.class));
        assertTrue(outContent.toString().contains("Product successfully added!"));
    }

    @Test
    @DisplayName("Should print message when view users list is empty")
    void testHandleViewUsers_Empty() {
        when(userServiceMock.getAllUsers()).thenReturn(Collections.emptyList());

        Scanner scanner = new Scanner("3\n0\n");
        staffMenu.menu(scanner);

        assertTrue(outContent.toString().contains("No users registered yet."));
    }

    @Test
    @DisplayName("Should view user profile and fetch cart/history correctly")
    void testHandleViewUsers_SubMenuFlow() {
        User user = new User();
        user.setId(1L);

        when(userServiceMock.getAllUsers()).thenReturn(List.of(user));
        when(userServiceMock.getUserById(1L)).thenReturn(user);
        when(bucketServiceMock.getProductsFromBucket(1L)).thenReturn(Collections.emptyList());
        when(orderServiceMock.getOrderHistory(1L)).thenReturn(Collections.emptyList());

        // Flow: 3 (View Users) -> 1 (User ID) -> 1 (View Cart) -> 2 (Purchase History) -> 0 -> 0
        Scanner scanner = new Scanner("3\n1\n1\n2\n0\n0\n");
        staffMenu.menu(scanner);

        assertTrue(outContent.toString().contains("Cart is empty."));
        assertTrue(outContent.toString().contains("History is empty."));
    }

    @Test
    @DisplayName("Should manage categories - Add new category")
    void testHandleManageCategories_Add() {
        // Flow: 4 (Manage Categories) -> 1 (Add) -> "Phones" -> 0 (Back) -> 0 (Sign out)
        Scanner scanner = new Scanner("4\n1\nPhones\n0\n0\n");
        staffMenu.menu(scanner);

        verify(categoryServiceMock, times(1)).addNewCategory("Phones");
        assertTrue(outContent.toString().contains("Category successfully added!"));
    }
}