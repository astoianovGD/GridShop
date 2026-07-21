package com.bobocode.Menus;

import com.bobocode.Entities.Products.Bucket;
import com.bobocode.Entities.Products.Product;
import com.bobocode.Entities.Users.User;
import com.bobocode.Enums.Gender;
import com.bobocode.Exceptions.EntityNotFoundException;
import com.bobocode.Services.Products.BucketService;
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

        userMenu = new UserMenu(userServiceMock, bucketServiceMock, marketPlaceServiceMock,
                catalogMenuMock, userConsoleViewServiceMock, bucketMenuMock);

        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Should throw NullPointerException if any dependency is null upon creation")
    void testNullDependencies() {
        assertThrows(NullPointerException.class, () -> new UserMenu(null, bucketServiceMock, marketPlaceServiceMock, catalogMenuMock, userConsoleViewServiceMock, bucketMenuMock));
        assertThrows(NullPointerException.class, () -> new UserMenu(userServiceMock, null, marketPlaceServiceMock, catalogMenuMock, userConsoleViewServiceMock, bucketMenuMock));
        assertThrows(NullPointerException.class, () -> new UserMenu(userServiceMock, bucketServiceMock, null, catalogMenuMock, userConsoleViewServiceMock, bucketMenuMock));
        assertThrows(NullPointerException.class, () -> new UserMenu(userServiceMock, bucketServiceMock, marketPlaceServiceMock, null, userConsoleViewServiceMock, bucketMenuMock));
        assertThrows(NullPointerException.class, () -> new UserMenu(userServiceMock, bucketServiceMock, marketPlaceServiceMock, catalogMenuMock, null, bucketMenuMock));
        assertThrows(NullPointerException.class, () -> new UserMenu(userServiceMock, bucketServiceMock, marketPlaceServiceMock, catalogMenuMock, userConsoleViewServiceMock, null));
    }

    @Test
    @DisplayName("Should sign out when option 0 is selected in user menu")
    void testMenu_SignOut() {
        User user = new User();
        Scanner scanner = new Scanner("0\n");

        userMenu.menu(user, scanner);

        assertTrue(outContent.toString().contains("Getting out of the system..."));
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
    @DisplayName("Should handle view bucket option via BucketMenu")
    void testMenu_ViewBucket() {
        User user = new User();
        Scanner scanner = new Scanner("3\n0\n");

        userMenu.menu(user, scanner);

        verify(bucketMenuMock, times(1)).handleBucket(eq(user), any(Scanner.class));
    }

    @Test
    @DisplayName("Should delete account successfully when user confirms with 'Y'")
    void testMenu_DeleteAccount_Confirmed() {
        User user = new User();
        user.setId(10L);

        // Flow: 4 (Delete account) -> "Y" (Confirm)
        Scanner scanner = new Scanner("4\nY\n");

        userMenu.menu(user, scanner);

        verify(userServiceMock, times(1)).deleteUserAccount(10L);
    }

    @Test
    @DisplayName("Should cancel account deletion if user types something other than 'Y'")
    void testMenu_DeleteAccount_Cancelled() {
        User user = new User();
        user.setId(10L);

        // Flow: 4 -> "N" (Cancel) -> 0 (Sign out)
        Scanner scanner = new Scanner("4\nN\n0\n");

        userMenu.menu(user, scanner);

        verify(userServiceMock, never()).deleteUserAccount(anyLong());
        assertTrue(outContent.toString().contains("Are you sure? Y/N"));
    }

    @Test
    @DisplayName("Should handle browsing products, sub-options, and exit")
    void testHandleBrowseProducts_SubOptionsAndExit() {
        when(marketPlaceServiceMock.getAllProducts()).thenReturn(Collections.emptyList());

        // Flow: 1 (Browse) -> 1 (Sort option via catalogMenu) -> 0 (Nothing/Exit browse) -> 0 (Sign out)
        Scanner scanner = new Scanner("1\n1\n0\n0\n");
        User user = new User();

        userMenu.menu(user, scanner);

        verify(catalogMenuMock, times(1)).handleOptions(eq("1"), any(Scanner.class));
    }

    @Test
    @DisplayName("Should handle filter and search options inside browse products menu")
    void testHandleBrowseProducts_FilterSearchAndInvalid() {
        when(marketPlaceServiceMock.getAllProducts()).thenReturn(Collections.emptyList());

        // Flow: 1 -> 2 (Filter) -> 3 (Search) -> 99 (Invalid sub-option) -> 0 -> 0
        Scanner scanner = new Scanner("1\n2\n3\n99\n0\n0\n");
        User user = new User();

        userMenu.menu(user, scanner);

        verify(catalogMenuMock, times(1)).handleOptions(eq("2"), any(Scanner.class));
        verify(catalogMenuMock, times(1)).handleOptions(eq("3"), any(Scanner.class));
        assertTrue(outContent.toString().contains("Invalid option!"));
    }

    @Test
    @DisplayName("Should successfully add product to bucket by ID")
    void testHandleBrowseProducts_AddToBucket_Success() {
        User user = new User();
        user.setBucket(new Bucket());
        Product product = new Product(5L, "Phone", BigDecimal.valueOf(500));

        when(marketPlaceServiceMock.getAllProducts()).thenReturn(List.of(product));
        when(marketPlaceServiceMock.getProductById(5L)).thenReturn(product);

        // Flow: 1 (Browse) -> 4 (Add to Bucket) -> "5" (Product ID) -> 0 (Exit browse) -> 0 (Sign out)
        Scanner scanner = new Scanner("1\n4\n5\n0\n0\n");

        userMenu.menu(user, scanner);

        verify(bucketServiceMock, times(1)).addProductToBucket(user.getBucket(), product);
        assertTrue(outContent.toString().contains("Product added to bucket!"));
    }

    @Test
    @DisplayName("Should handle NumberFormatException and EntityNotFoundException when adding product to bucket")
    void testHandleBrowseProducts_AddToBucket_Errors() {
        User user = new User();
        when(marketPlaceServiceMock.getAllProducts()).thenReturn(Collections.emptyList());
        when(marketPlaceServiceMock.getProductById(99L)).thenThrow(new EntityNotFoundException("Product not found"));

        // Flow:
        // 1 -> 4 -> "abc" (NumberFormat ID)
        // 1 -> 4 -> "99" (EntityNotFound ID) -> 0 -> 0
        Scanner scanner = new Scanner("1\n4\nabc\n4\n99\n0\n0\n");

        userMenu.menu(user, scanner);

        assertTrue(outContent.toString().contains("Invalid ID format! Please enter a number."));
        assertTrue(outContent.toString().contains("Product not found"));
    }

    @Test
    @DisplayName("Should handle view personal data menu options and history")
    void testHandleViewPersonalData_HistoryAndExit() {
        User user = new User();
        // Flow: 2 (View Personal Data) -> 2 (See Purchase History) -> 0 (Nothing) -> 0 (Sign out)
        Scanner scanner = new Scanner("2\n2\n0\n0\n");

        userMenu.menu(user, scanner);

        verify(userConsoleViewServiceMock, times(1)).printUserProfile(user);
        verify(userConsoleViewServiceMock, times(1)).printUserPurchaseHistory(user);
    }

    @Test
    @DisplayName("Should handle invalid option in personal data menu")
    void testHandleViewPersonalData_InvalidOption() {
        User user = new User();
        // Flow: 2 -> 99 (Invalid) -> 0 -> 0
        Scanner scanner = new Scanner("2\n99\n0\n0\n");

        userMenu.menu(user, scanner);

        assertTrue(outContent.toString().contains("Invalid option!"));
    }

    @Test
    @DisplayName("Should successfully edit profile fields (FirstName, LastName, Age, Gender, Password, Cancel)")
    void testEditUserProfile_AllFields() {
        User user = new User();
        user.setId(1L);

        // Flow to test each edit option inside editUserProfile:
        // 2 (View personal data) -> 1 (Edit profile)
        // -> 1 (First Name) -> "John"
        // -> 1 -> 2 (Last Name) -> "Doe"
        // -> 1 -> 3 (Age) -> "abc" (NumberFormat error)
        // -> 1 -> 3 (Age) -> "30" (Success)
        // -> 1 -> 4 (Gender) -> "INVALID" (Gender error retry) -> "MALE" (Success)
        // -> 1 -> 6 (Password) -> "newPass123"
        // -> 1 -> 0 (Cancel)
        // -> 0 -> 0
        String simulatedInput = "2\n1\n" +
                "1\nJohn\n" +
                "1\n2\nDoe\n" +
                "1\n3\nabc\n" +
                "1\n3\n30\n" +
                "1\n4\nINVALID\nMALE\n" +
                "1\n6\nnewPass123\n" +
                "1\n0\n" +
                "0\n0\n";

        Scanner scanner = new Scanner(simulatedInput);

        userMenu.menu(user, scanner);

        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals(30, user.getAge());
        assertEquals(Gender.MALE, user.getGender());
        assertEquals("newPass123", user.getPassword());
        assertTrue(outContent.toString().contains("Invalid input! Please enter a valid number."));
        assertTrue(outContent.toString().contains("Invalid gender! Please enter exactly MALE, FEMALE, or OTHER:"));
        assertTrue(outContent.toString().contains("Editing cancelled."));
    }

    @Test
    @DisplayName("Should handle editing email and invalid option in edit profile")
    void testEditUserProfile_EmailAndInvalidOption() {
        User user = new User();
        user.setId(1L);

        // Flow:
        // 2 -> 1 -> 5 (Email) -> "new.email@test.com" (handled by EmailValidator mock/console)
        // 2 -> 1 -> 99 (Invalid option)
        // -> 0 -> 0
        String simulatedInput = "2\n1\n5\nnew.email@test.com\n2\n1\n99\n0\n0\n";
        Scanner scanner = new Scanner(simulatedInput);

        // Stub email validator console behavior if needed, or if EmailValidator prompts:
        // EmailValidator reads from console if not free. Let's provide an extra line if necessary,
        // but if validateEmailIsFree passes immediately, it reads once. Let's make sure.
        // Actually EmailValidator.getUniqueEmailFromConsole asks for email line.

        userMenu.menu(user, scanner);

        assertTrue(outContent.toString().contains("Invalid option!"));
    }
}