package com.bobocode.Menus;

import com.bobocode.Entities.Products.Bucket;
import com.bobocode.Entities.Products.Product;
import com.bobocode.Entities.Users.User;
import com.bobocode.Exceptions.EntityNotFoundException;
import com.bobocode.Services.Products.MarketPlaceService;
import com.bobocode.Services.User.UserConsoleViewService;
import com.bobocode.Services.User.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
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
    private StaffMenu staffMenu;

    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        userServiceMock = mock(UserService.class);
        marketPlaceServiceMock = mock(MarketPlaceService.class);
        catalogMenuMock = mock(CatalogMenu.class);
        userConsoleViewServiceMock = mock(UserConsoleViewService.class);

        staffMenu = new StaffMenu(userServiceMock, marketPlaceServiceMock, catalogMenuMock, userConsoleViewServiceMock);

        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Should throw NullPointerException if any dependency is null upon creation")
    void testNullDependencies() {
        assertThrows(NullPointerException.class, () -> new StaffMenu(null, marketPlaceServiceMock, catalogMenuMock, userConsoleViewServiceMock));
        assertThrows(NullPointerException.class, () -> new StaffMenu(userServiceMock, null, catalogMenuMock, userConsoleViewServiceMock));
        assertThrows(NullPointerException.class, () -> new StaffMenu(userServiceMock, marketPlaceServiceMock, null, userConsoleViewServiceMock));
        assertThrows(NullPointerException.class, () -> new StaffMenu(userServiceMock, marketPlaceServiceMock, catalogMenuMock, null));
    }

    @Test
    @DisplayName("Should sign out when option 0 is selected in staff menu")
    void testMenu_SignOut() {
        Scanner scanner = new Scanner("0\n");
        staffMenu.menu(scanner);
        assertTrue(outContent.toString().contains("--- Staff Menu ---"));
    }

    @Test
    @DisplayName("Should handle invalid option in staff menu then sign out")
    void testMenu_InvalidOptionThenSignOut() {
        Scanner scanner = new Scanner("99\n0\n");
        staffMenu.menu(scanner);
        assertTrue(outContent.toString().contains("Invalid option!"));
    }

    @Test
    @DisplayName("Should successfully add a new product")
    void testHandleAddProduct_Success() {
        // Flow: 1 (Add Product) -> Name -> Price -> 0 (Sign out)
        Scanner scanner = new Scanner("1\nKeyboard\n45.99\n0\n");
        staffMenu.menu(scanner);

        verify(marketPlaceServiceMock, times(1)).addNewProduct(any(Product.class));
        assertTrue(outContent.toString().contains("Product successfully added!"));
    }

    @Test
    @DisplayName("Should handle NumberFormatException when entering invalid price format for product addition")
    void testHandleAddProduct_InvalidPriceFormat() {
        // Flow: 1 -> Name -> "abc" (invalid price) -> 0
        Scanner scanner = new Scanner("1\nKeyboard\nabc\n0\n");
        staffMenu.menu(scanner);

        verify(marketPlaceServiceMock, never()).addNewProduct(any());
        assertTrue(outContent.toString().contains("Invalid price format! Product not added."));
    }

    @Test
    @DisplayName("Should handle browsing products with sort option and exit")
    void testHandleBrowseProducts_SortAndExit() {
        when(marketPlaceServiceMock.getAllProducts()).thenReturn(Collections.emptyList());

        // Flow: 2 (Browse Products) -> 1 (Sort option handled by catalogMenu) -> 0 (Nothing/Exit browse) -> 0 (Sign out)
        Scanner scanner = new Scanner("2\n1\n0\n0\n");
        staffMenu.menu(scanner);

        verify(catalogMenuMock, times(1)).handleOptions(eq("1"), any(Scanner.class));
    }

    @Test
    @DisplayName("Should handle filter and search options inside browse products")
    void testHandleBrowseProducts_FilterAndSearch() {
        when(marketPlaceServiceMock.getAllProducts()).thenReturn(Collections.emptyList());

        // Flow: 2 -> 2 (Filter) -> 3 (Search) -> 99 (Invalid sub-option) -> 0 -> 0
        Scanner scanner = new Scanner("2\n2\n3\n99\n0\n0\n");
        staffMenu.menu(scanner);

        verify(catalogMenuMock, times(1)).handleOptions(eq("2"), any(Scanner.class));
        verify(catalogMenuMock, times(1)).handleOptions(eq("3"), any(Scanner.class));
        assertTrue(outContent.toString().contains("Invalid option!"));
    }

    @Test
    @DisplayName("Should successfully remove a product")
    void testHandleRemoveProduct_Success() {
        when(marketPlaceServiceMock.getAllProducts()).thenReturn(Collections.emptyList());

        // Flow: 2 -> 4 (Remove) -> 10L (ID) -> 0 -> 0
        Scanner scanner = new Scanner("2\n4\n10\n0\n0\n");
        staffMenu.menu(scanner);

        verify(marketPlaceServiceMock, times(1)).removeProduct(10L);
        assertTrue(outContent.toString().contains("Product successfully deleted!"));
    }

    @Test
    @DisplayName("Should handle NumberFormatException and EntityNotFoundException during product removal")
    void testHandleRemoveProduct_Errors() {
        when(marketPlaceServiceMock.getAllProducts()).thenReturn(Collections.emptyList());
        doThrow(new EntityNotFoundException("Product not found!")).when(marketPlaceServiceMock).removeProduct(99L);

        // Flow: 2 -> 4 -> "abc" (NumberFormat)
        // 2 -> 4 -> "99" (EntityNotFound) -> 0 -> 0
        Scanner scanner = new Scanner("2\n4\nabc\n4\n99\n0\n0\n");
        staffMenu.menu(scanner);

        assertTrue(outContent.toString().contains("Invalid ID format! Please enter a valid number."));
        assertTrue(outContent.toString().contains("Product not found!"));
    }

    @Test
    @DisplayName("Should successfully edit product name")
    void testHandleEditProduct_EditName() {
        Product product = new Product(1L, "OldName", BigDecimal.TEN);
        when(marketPlaceServiceMock.getAllProducts()).thenReturn(List.of(product));
        when(marketPlaceServiceMock.getProductById(1L)).thenReturn(product);

        // Flow: 2 -> 5 (Edit) -> 1 (ID) -> 1 (Edit Name) -> "NewName" -> 0 -> 0
        Scanner scanner = new Scanner("2\n5\n1\n1\nNewName\n0\n0\n");
        staffMenu.menu(scanner);

        assertEquals("NewName", product.getName());
    }

    @Test
    @DisplayName("Should successfully edit product price and handle invalid price format")
    void testHandleEditProduct_EditPrice() {
        Product product = new Product(1L, "Item", BigDecimal.TEN);
        when(marketPlaceServiceMock.getAllProducts()).thenReturn(List.of(product));
        when(marketPlaceServiceMock.getProductById(1L)).thenReturn(product);

        // Flow:
        // 2 -> 5 -> 1 -> 2 (Edit Price) -> "abc" (Invalid format)
        // 2 -> 5 -> 1 -> 2 (Edit Price) -> "99.99" (Success) -> 0 -> 0
        Scanner scanner = new Scanner("2\n5\n1\n2\nabc\n5\n1\n2\n99.99\n0\n0\n");
        staffMenu.menu(scanner);

        assertTrue(outContent.toString().contains("Invalid price format! Please enter a valid number."));
        assertTrue(outContent.toString().contains("Price successfully updated!"));
        assertEquals(new BigDecimal("99.99"), product.getPrice());
    }

    @Test
    @DisplayName("Should handle invalid option and edit errors for product")
    void testHandleEditProduct_ErrorsAndInvalidOption() {
        Product product = new Product(1L, "Item", BigDecimal.TEN);
        when(marketPlaceServiceMock.getAllProducts()).thenReturn(List.of(product));
        when(marketPlaceServiceMock.getProductById(1L)).thenReturn(product);

        // Flow:
        // 2 -> 5 -> "abc" (NumberFormat ID)
        // 2 -> 5 -> "99" (EntityNotFound ID)
        // 2 -> 5 -> 1 -> 99 (Invalid sub-option) -> 0 -> 0
        Scanner scanner = new Scanner("2\n5\nabc\n5\n99\n5\n1\n99\n0\n0\n");
        when(marketPlaceServiceMock.getProductById(99L)).thenThrow(new EntityNotFoundException("Not found"));

        staffMenu.menu(scanner);

        assertTrue(outContent.toString().contains("Invalid ID format! Please enter a valid number."));
        assertTrue(outContent.toString().contains("Not found"));
        assertTrue(outContent.toString().contains("Invalid Option!"));
    }

    @Test
    @DisplayName("Should print message when view users list is empty")
    void testHandleViewUsers_Empty() {
        when(userServiceMock.getAllUsers()).thenReturn(Collections.emptyList());

        // Flow: 3 (View Users) -> 0 (Sign out)
        Scanner scanner = new Scanner("3\n0\n");
        staffMenu.menu(scanner);

        assertTrue(outContent.toString().contains("No users registered yet."));
    }

    @Test
    @DisplayName("Should successfully view user profile and handle sub-menu options")
    void testHandleViewUsers_SubMenuFlow() {
        User user = new User();
        user.setId(1L);
        user.setBucket(new Bucket());
        user.setPurchaseHistory(new ArrayList<>());

        when(userServiceMock.getAllUsers()).thenReturn(List.of(user));
        when(userServiceMock.getUserById(1L)).thenReturn(user);

        // Flow:
        // 3 (View Users) -> 1 (User ID)
        // -> Submenu: 1 (View Cart - empty)
        // -> Submenu: 2 (Purchase History - empty)
        // -> Submenu: 99 (Invalid option)
        // -> Submenu: 0 (Return to menu) -> 0 (Sign out)
        Scanner scanner = new Scanner("3\n1\n1\n2\n99\n0\n0\n");
        staffMenu.menu(scanner);

        assertTrue(outContent.toString().contains("Cart is empty."));
        assertTrue(outContent.toString().contains("History is empty."));
        assertTrue(outContent.toString().contains("Invalid option!"));
    }

    @Test
    @DisplayName("Should handle cart with products and purchase history in user sub-menu")
    void testHandleViewUsers_SubMenuPopulated() {
        User user = new User();
        user.setId(1L);

        Bucket bucket = new Bucket();
        bucket.getProductsInBucket().add(new Product(1L, "Shirt", BigDecimal.TEN));
        user.setBucket(bucket);

        List<Bucket> history = new ArrayList<>();
        history.add(bucket);
        user.setPurchaseHistory(history);

        when(userServiceMock.getAllUsers()).thenReturn(List.of(user));
        when(userServiceMock.getUserById(1L)).thenReturn(user);

        // Flow: 3 -> 1 -> 1 (View Cart - populated) -> 2 (History - populated) -> 0 -> 0
        Scanner scanner = new Scanner("3\n1\n1\n2\n0\n0\n");
        staffMenu.menu(scanner);

        verify(catalogMenuMock, times(1)).catalogAllProducts(anyList());
        verify(userConsoleViewServiceMock, times(1)).printUserPurchaseHistory(user);
    }

    @Test
    @DisplayName("Should handle NumberFormatException and EntityNotFoundException during user selection")
    void testHandleViewUsers_Errors() {
        User user = new User();
        user.setId(1L);
        when(userServiceMock.getAllUsers()).thenReturn(List.of(user));
        when(userServiceMock.getUserById(55L)).thenThrow(new EntityNotFoundException("User not found"));

        // Flow:
        // 3 -> "abc" (NumberFormat ID)
        // 3 -> "55" (EntityNotFound ID) -> 0
        Scanner scanner = new Scanner("3\nabc\n3\n55\n0\n");
        staffMenu.menu(scanner);

        assertTrue(outContent.toString().contains("Invalid ID format! Please enter a valid number."));
        assertTrue(outContent.toString().contains("User not found"));
    }
}