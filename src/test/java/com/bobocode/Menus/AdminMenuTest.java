package com.bobocode.Menus;

import com.bobocode.Entities.Users.Staff;
import com.bobocode.Exceptions.EntityNotFoundException;
import com.bobocode.Services.User.StaffService;
import com.bobocode.Services.User.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminMenuTest {

    private StaffService staffServiceMock;
    private UserService userServiceMock;
    private AdminMenu adminMenu;

    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        staffServiceMock = mock(StaffService.class);
        userServiceMock = mock(UserService.class);
        adminMenu = new AdminMenu(staffServiceMock, userServiceMock);

        // Intercept console output
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Should throw NullPointerException immediately if dependencies are null")
    void testNullDependencies() {
        // Ensure you have added @NonNull to fields in AdminMenu
        assertThrows(NullPointerException.class, () -> new AdminMenu(null, userServiceMock));
        assertThrows(NullPointerException.class, () -> new AdminMenu(staffServiceMock, null));
    }

    @Test
    @DisplayName("Should sign out when option 0 is selected in main menu")
    void testMenu_SignOut() {
        // Arrange
        String simulatedInput = "0\n";
        Scanner scanner = new Scanner(simulatedInput);

        // Act
        adminMenu.menu(scanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Signing out..."));
    }

    @Test
    @DisplayName("Should handle invalid option in main menu then sign out")
    void testMenu_InvalidOptionThenSignOut() {
        // Arrange
        String simulatedInput = "99\n0\n";
        Scanner scanner = new Scanner(simulatedInput);

        // Act
        adminMenu.menu(scanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Invalid option!"));
        assertTrue(output.contains("Signing out..."));
    }

    @Test
    @DisplayName("Should print message when managing staff and list is empty")
    void testHandleManageStaff_EmptyList() {
        // Arrange
        when(staffServiceMock.getAllStaff()).thenReturn(Collections.emptyList());

        // Main menu option 1 -> View Staff (list empty, exits back to main), then 0 -> Sign out
        String simulatedInput = "1\n0\n";
        Scanner scanner = new Scanner(simulatedInput);

        // Act
        adminMenu.menu(scanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Staff list is empty! Add someone first."));
    }

    @Test
    @DisplayName("Should handle deleting staff successfully")
    void testHandleDeleteStaff_Success() {
        // Arrange
        Staff staff = new Staff();
        staff.setId(1L);
        when(staffServiceMock.getAllStaff()).thenReturn(List.of(staff));

        // Flow:
        // 1 (View Staff) -> 2 (Delete Staff) -> "1" (ID to delete) -> 0 (Nothing/Exit manage) -> 0 (Sign out)
        String simulatedInput = "1\n2\n1\n0\n0\n";
        Scanner scanner = new Scanner(simulatedInput);

        doNothing().when(staffServiceMock).removeStaff(1L);

        // Act
        adminMenu.menu(scanner);

        // Assert
        verify(staffServiceMock, times(1)).removeStaff(1L);
        assertTrue(outContent.toString().contains("Staff successfully deleted!"));
    }

    @Test
    @DisplayName("Should handle NumberFormatException when entering invalid ID format for deletion")
    void testHandleDeleteStaff_InvalidIdFormat() {
        // Arrange
        Staff staff = new Staff();
        staff.setId(1L);
        when(staffServiceMock.getAllStaff()).thenReturn(List.of(staff));

        // Flow: 1 -> 2 -> "abc" (invalid text instead of long ID) -> 0 -> 0
        String simulatedInput = "1\n2\nabc\n0\n0\n";
        Scanner scanner = new Scanner(simulatedInput);

        // Act
        adminMenu.menu(scanner);

        // Assert
        assertTrue(outContent.toString().contains("Invalid ID format! Please enter a number."));
        verify(staffServiceMock, never()).removeStaff(anyLong());
    }

    @Test
    @DisplayName("Should handle EntityNotFoundException or IllegalArgumentException during deletion")
    void testHandleDeleteStaff_Exceptions() {
        // Arrange
        Staff staff = new Staff();
        staff.setId(99L);
        when(staffServiceMock.getAllStaff()).thenReturn(List.of(staff));

        // Flow: 1 -> 2 -> "99" -> 0 -> 0
        String simulatedInput = "1\n2\n99\n0\n0\n";
        Scanner scanner = new Scanner(simulatedInput);

        doThrow(new EntityNotFoundException("Staff not found!")).when(staffServiceMock).removeStaff(99L);

        // Act
        adminMenu.menu(scanner);

        // Assert
        assertTrue(outContent.toString().contains("Staff not found!"));
    }

    @Test
    @DisplayName("Should handle editing staff successfully (First Name)")
    void testHandleEditStaff_SuccessFirstName() {
        // Arrange
        Staff staff = new Staff();
        staff.setId(5L);
        staff.setFirstName("OldName");

        when(staffServiceMock.getAllStaff()).thenReturn(List.of(staff));
        when(staffServiceMock.getStaffById(5L)).thenReturn(staff);

        // Flow:
        // 1 (Manage) -> 1 (Edit Staff) -> "5" (ID) -> 1 (Edit First Name) -> "NewName" -> 0 (Exit manage) -> 0 (Sign out)
        String simulatedInput = "1\n1\n5\n1\nNewName\n0\n0\n";
        Scanner scanner = new Scanner(simulatedInput);

        // Act
        adminMenu.menu(scanner);

        // Assert
        assertEquals("NewName", staff.getFirstName());
        verify(staffServiceMock, times(1)).editStaff(5L, staff);
        assertTrue(outContent.toString().contains("Staff profile updated!"));
    }

    @Test
    @DisplayName("Should handle editing staff (Email update with validation)")
    void testHandleEditStaff_Email() {
        // Arrange
        Staff staff = new Staff();
        staff.setId(5L);

        when(staffServiceMock.getAllStaff()).thenReturn(List.of(staff));
        when(staffServiceMock.getStaffById(5L)).thenReturn(staff);
        doNothing().when(userServiceMock).validateEmailIsFree("new@mail.com");

        // Flow:
        // 1 -> 1 -> "5" -> 3 (Edit Email) -> "new@mail.com" -> 0 -> 0
        String simulatedInput = "1\n1\n5\n3\nnew@mail.com\n0\n0\n";
        Scanner scanner = new Scanner(simulatedInput);

        // Act
        adminMenu.menu(scanner);

        // Assert
        assertEquals("new@mail.com", staff.getEmail());
        assertTrue(outContent.toString().contains("Email updated!"));
    }

    @Test
    @DisplayName("Should handle cancel option during edit staff info")
    void testEditStaffInfo_Cancel() {
        // Arrange
        Staff staff = new Staff();
        staff.setId(5L);

        when(staffServiceMock.getAllStaff()).thenReturn(List.of(staff));
        when(staffServiceMock.getStaffById(5L)).thenReturn(staff);

        // Flow:
        // 1 (Manage) -> 1 (Edit Staff) -> "5" (ID) -> 0 (Cancel editing)
        // -> 0 (Exit manage menu) -> 0 (Sign out from main menu)
        String simulatedInput = "1\n1\n5\n0\n0\n0\n";
        Scanner scanner = new Scanner(simulatedInput);

        // Act
        adminMenu.menu(scanner);

        // Assert
        assertTrue(outContent.toString().contains("Editing cancelled."));
    }

    @Test
    @DisplayName("Should handle invalid option during edit staff info")
    void testEditStaffInfo_InvalidOption() {
        // Arrange
        Staff staff = new Staff();
        staff.setId(5L);

        when(staffServiceMock.getAllStaff()).thenReturn(List.of(staff));
        when(staffServiceMock.getStaffById(5L)).thenReturn(staff);

        // Flow: 1 -> 1 -> "5" -> 99 (Invalid sub-option) -> 0 -> 0
        String simulatedInput = "1\n1\n5\n99\n0\n0\n";
        Scanner scanner = new Scanner(simulatedInput);

        // Act
        adminMenu.menu(scanner);

        // Assert
        assertTrue(outContent.toString().contains("Invalid Option!"));
    }

    @Test
    @DisplayName("Should handle invalid ID format and entity not found during edit")
    void testHandleEditStaff_Errors() {
        // Arrange
        Staff staff = new Staff();
        staff.setId(5L);
        when(staffServiceMock.getAllStaff()).thenReturn(List.of(staff));
        when(staffServiceMock.getStaffById(100L)).thenThrow(new EntityNotFoundException("Not found"));

        // Flow:
        // 1 -> 1 -> "abc" (NumberFormatException)
        // 1 -> 1 -> "100" (EntityNotFoundException) -> 0 -> 0
        String simulatedInput = "1\n1\nabc\n1\n100\n0\n0\n";
        Scanner scanner = new Scanner(simulatedInput);

        // Act
        adminMenu.menu(scanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Invalid ID format! Please enter a number."));
        assertTrue(output.contains("Not found"));
    }

    @Test
    @DisplayName("Should successfully add a new staff member")
    void testHandleAddNewStaff_Success() {
        // Arrange
        // Flow:
        // 2 (Add New Staff from main menu) -> FirstName -> LastName -> Email -> Password -> 0 (Sign out)
        String simulatedInput = "2\nJohn\nDoe\njohn.doe@test.com\nsecretPass\n0\n";
        Scanner scanner = new Scanner(simulatedInput);

        doNothing().when(userServiceMock).validateEmailIsFree("john.doe@test.com");

        // Act
        adminMenu.menu(scanner);

        // Assert
        verify(staffServiceMock, times(1)).addNewStaff(any(Staff.class));
        assertTrue(outContent.toString().contains("Staff was successfully added!"));
    }
}