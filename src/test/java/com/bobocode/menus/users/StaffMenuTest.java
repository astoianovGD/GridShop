package com.bobocode.menus.users;

import com.bobocode.dto.products.CategoryDto;
import com.bobocode.dto.products.ProductDto;
import com.bobocode.dto.users.UserDto;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.menus.products.CatalogMenu;
import com.bobocode.services.bucket.BucketConsoleViewService;
import com.bobocode.services.bucket.BucketService;
import com.bobocode.services.orders.OrderConsoleViewService;
import com.bobocode.services.orders.OrderService;
import com.bobocode.services.products.CategoryService;
import com.bobocode.services.products.MarketPlaceService;
import com.bobocode.services.products.ProductsConsoleViewService;
import com.bobocode.services.user.UserConsoleViewService;
import com.bobocode.services.user.UserService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StaffMenuTest {

    @Mock private UserService userService;
    @Mock private MarketPlaceService marketPlaceService;
    @Mock private CatalogMenu catalogMenu;
    @Mock private UserConsoleViewService userConsoleViewService;
    @Mock private BucketService bucketService;
    @Mock private OrderService orderService;
    @Mock private CategoryService categoryService;
    @Mock private ProductsConsoleViewService productsConsoleViewService;
    @Mock private BucketConsoleViewService bucketConsoleViewService;
    @Mock private OrderConsoleViewService orderConsoleViewService;

    @InjectMocks
    private StaffMenu staffMenu;

    @Test
    void shouldExitStaffMenuSuccessfully() {
        Scanner scanner = createScanner("0\n");

        staffMenu.menu(scanner);

        verifyNoInteractions(userService, marketPlaceService, categoryService);
    }

    @Test
    void shouldHandleInvalidMenuOptionThenExit() {
        Scanner scanner = createScanner("invalid\n0\n");

        staffMenu.menu(scanner);

        verifyNoInteractions(userService, marketPlaceService, categoryService);
    }

    @Test
    void shouldHandleAddProductWhenNoCategoriesFound() {
        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());

        // Input: "1" (Add Product), "0" (Sign out)
        Scanner scanner = createScanner("1\n0\n");

        staffMenu.menu(scanner);

        verify(categoryService).getAllCategories();
        verifyNoInteractions(marketPlaceService);
    }

    @Test
    void shouldAddProductSuccessfully() {
        CategoryDto category = new CategoryDto();
        category.setId(1L);
        category.setName("Electronics");

        when(categoryService.getAllCategories()).thenReturn(List.of(category));
        when(categoryService.isCategoryExists(1L)).thenReturn(true);

        // Input: "1" (Add Product), "1" (Category ID), "Phone" (Name), "999.99" (Price), "0" (Sign out)
        Scanner scanner = createScanner("1\n1\nPhone\n999.99\n0\n");

        staffMenu.menu(scanner);

        verify(marketPlaceService).addNewProduct(any());
    }

    @Test
    void shouldBrowseProductsAndExitSubmenu() {
        ProductDto product = new ProductDto();
        when(marketPlaceService.getAllProducts()).thenReturn(List.of(product));

        // Input: "2" (Browse Products), "0" (Nothing), "0" (Sign out)
        Scanner scanner = createScanner("2\n0\n0\n");

        staffMenu.menu(scanner);

        verify(productsConsoleViewService).catalogAllProducts(anyList());
    }

    @Test
    void shouldRemoveProductWithAffectedUsers() {
        ProductDto product = new ProductDto();
        when(marketPlaceService.getAllProducts()).thenReturn(List.of(product));

        UserDto affectedUser = new UserDto();
        affectedUser.setId(10L);
        affectedUser.setFirstname("John");
        affectedUser.setLastname("Doe");

        when(marketPlaceService.removeProduct(5L)).thenReturn(List.of(affectedUser));

        // Input: "2" (Browse), "4" (Remove), "5" (ID), "0" (Nothing), "0" (Sign out)
        Scanner scanner = createScanner("2\n4\n5\n0\n0\n");

        staffMenu.menu(scanner);

        verify(marketPlaceService).removeProduct(5L);
    }

    @Test
    void shouldHandleEntityNotFoundDuringProductRemoval() {
        ProductDto product = new ProductDto();
        when(marketPlaceService.getAllProducts()).thenReturn(List.of(product));
        doThrow(new EntityNotFoundException("Product not found")).when(marketPlaceService).removeProduct(99L);

        // Input: "2" (Browse), "4" (Remove), "99" (ID), "0" (Nothing), "0" (Sign out)
        Scanner scanner = createScanner("2\n4\n99\n0\n0\n");

        staffMenu.menu(scanner);

        verify(marketPlaceService).removeProduct(99L);
    }

    @Test
    void shouldEditProductNameSuccessfully() {
        ProductDto product = new ProductDto();
        product.setId(2L);
        when(marketPlaceService.getAllProducts()).thenReturn(List.of(product));
        when(marketPlaceService.getProductById(2L)).thenReturn(product);

        // Input: "2" (Browse), "5" (Edit), "2" (ID), "1" (Edit Name), "NewName", "0" (Nothing), "0" (Sign out)
        Scanner scanner = createScanner("2\n5\n2\n1\nNewName\n0\n0\n");

        staffMenu.menu(scanner);

        verify(marketPlaceService).updateProductField(eq(2L), any());
    }

    @Test
    void shouldHandleViewUsersEmptyList() {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        // Input: "3" (View Users), "0" (Sign out)
        Scanner scanner = createScanner("3\n0\n");

        staffMenu.menu(scanner);

        verify(userService).getAllUsers();
        verifyNoInteractions(userConsoleViewService);
    }

    @Test
    void shouldViewUserSubMenuCartAndOrders() {
        UserDto user = new UserDto();
        user.setId(1L);
        when(userService.getAllUsers()).thenReturn(List.of(user));
        when(userService.getUserById(1L)).thenReturn(user);

        // Input:
        // 3 (View Users)
        // 1 (User ID)
        // 1 (View Cart)
        // 2 (View Orders)
        // 0 (Return to menu)
        // 0 (Sign out)
        Scanner scanner = createScanner("3\n1\n1\n2\n0\n0\n");

        staffMenu.menu(scanner);

        verify(bucketService).getProductsFromBucket(1L);
        verify(orderService).getUserOrders(1L);
    }

    @Test
    void shouldAddNewCategorySuccessfully() {
        // Input: "4" (Manage Categories), "1" (Add Category), "Laptops" (Name), "0" (Go Back), "0" (Sign out)
        Scanner scanner = createScanner("4\n1\nLaptops\n0\n0\n");

        staffMenu.menu(scanner);

        verify(categoryService).addNewCategory(any());
    }

    @Test
    void shouldManageCategoriesViewAndEdit() {
        CategoryDto category = new CategoryDto();
        category.setId(1L);
        category.setName("OldName");

        when(categoryService.getAllCategories()).thenReturn(List.of(category));

        // Input:
        // 4 (Manage Categories)
        // 2 (View Categories)
        // 1 (Change Category)
        // 1 (Category ID)
        // "NewCategory" (New Name)
        // 2 (Remove Category)
        // 1 (Category ID to remove)
        // 0 (Go Back from View)
        // 0 (Go Back from Manage)
        // 0 (Sign out)
        Scanner scanner = createScanner("4\n2\n1\n1\nNewCategory\n2\n1\n0\n0\n0\n");

        staffMenu.menu(scanner);

        verify(categoryService).editCategory(any(), eq(1L));
        verify(categoryService).removeCategory(1L);
    }

    private Scanner createScanner(String input) {
        return new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
    }
}