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
        assertThrows(NullPointerException.class, () -> new AdminMenu(null, userServiceMock));
        assertThrows(NullPointerException.class, () -> new AdminMenu(staffServiceMock, null));
    }

    @Test
    @DisplayName("Should sign out when option 0 is selected in main menu")
    void testMenu_SignOut() {
        String simulatedInput = "0\n";
        Scanner scanner = new Scanner(simulatedInput);

        adminMenu.menu(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Signing out..."));
    }

    @Test
    @DisplayName("Should handle invalid option in main menu then sign out")
    void testMenu_InvalidOptionThenSignOut() {
        String simulatedInput = "99\n0\n";
        Scanner scanner = new Scanner(simulatedInput);

        adminMenu.menu(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Invalid option!"));
        assertTrue(output.contains("Signing out..."));
    }

    @Test
    @DisplayName("Should print message when managing staff and list is empty")
    void testHandleManageStaff_EmptyList() {
        when(staffServiceMock.getAllStaff()).thenReturn(Collections.emptyList());

        String simulatedInput = "1\n0\n";
        Scanner scanner = new Scanner(simulatedInput);

        adminMenu.menu(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Staff list is empty! Add someone first."));
    }

    @Test
    @DisplayName("Should handle deleting staff successfully")
    void testHandleDeleteStaff_Success() {
        Staff staff = new Staff();
        staff.setId(1L);
        when(staffServiceMock.getAllStaff()).thenReturn(List.of(staff));

        String simulatedInput = "1\n2\n1\n0\n0\n";
        Scanner scanner = new Scanner(simulatedInput);

        doNothing().when(staffServiceMock).removeStaff(1L);

        adminMenu.menu(scanner);

        verify(staffServiceMock, times(1)).removeStaff(1L);
        assertTrue(outContent.toString().contains("Staff successfully deleted!"));
    }

    @Test
    @DisplayName("Should handle NumberFormatException when entering invalid ID format for deletion")
    void testHandleDeleteStaff_InvalidIdFormat() {
        Staff staff = new Staff();
        staff.setId(1L);
        when(staffServiceMock.getAllStaff()).thenReturn(List.of(staff));

        // Flow: 1 -> 2 -> "abc" (invalid) -> "1" (valid ID to break loop) -> 0 -> 0
        String simulatedInput = "1\n2\nabc\n1\n0\n0\n";
        Scanner scanner = new Scanner(simulatedInput);

        adminMenu.menu(scanner);

        assertTrue(outContent.toString().contains("Invalid ID format! Please enter a valid number."));
        verify(staffServiceMock, times(1)).removeStaff(1L);
    }

    @Test
    @DisplayName("Should handle EntityNotFoundException or IllegalArgumentException during deletion")
    void testHandleDeleteStaff_Exceptions() {
        Staff staff = new Staff();
        staff.setId(99L);
        when(staffServiceMock.getAllStaff()).thenReturn(List.of(staff));

        String simulatedInput = "1\n2\n99\n0\n0\n";
        Scanner scanner = new Scanner(simulatedInput);

        doThrow(new EntityNotFoundException("Staff not found!")).when(staffServiceMock).removeStaff(99L);

        adminMenu.menu(scanner);

        assertTrue(outContent.toString().contains("Staff not found!"));
    }

    @Test
    @DisplayName("Should handle editing staff successfully (First Name)")
    void testHandleEditStaff_SuccessFirstName() {
        Staff staff = new Staff();
        staff.setId(5L);
        staff.setFirstName("OldName");

        when(staffServiceMock.getAllStaff()).thenReturn(List.of(staff));
        when(staffServiceMock.getStaffById(5L)).thenReturn(staff);

        String simulatedInput = "1\n1\n5\n1\nNewName\n0\n0\n";
        Scanner scanner = new Scanner(simulatedInput);

        adminMenu.menu(scanner);

        assertEquals("NewName", staff.getFirstName());
        verify(staffServiceMock, times(1)).editStaff(5L, staff);
        assertTrue(outContent.toString().contains("Staff profile updated!"));
    }

    @Test
    @DisplayName("Should handle editing staff (Email update with validation)")
    void testHandleEditStaff_Email() {
        Staff staff = new Staff();
        staff.setId(5L);

        when(staffServiceMock.getAllStaff()).thenReturn(List.of(staff));
        when(staffServiceMock.getStaffById(5L)).thenReturn(staff);
        doNothing().when(userServiceMock).validateEmailIsFree("new@mail.com");

        String simulatedInput = "1\n1\n5\n3\nnew@mail.com\n0\n0\n";
        Scanner scanner = new Scanner(simulatedInput);

        adminMenu.menu(scanner);

        assertEquals("new@mail.com", staff.getEmail());
        assertTrue(outContent.toString().contains("Email updated!"));
    }

    @Test
    @DisplayName("Should handle cancel option during edit staff info")
    void testEditStaffInfo_Cancel() {
        Staff staff = new Staff();
        staff.setId(5L);

        when(staffServiceMock.getAllStaff()).thenReturn(List.of(staff));
        when(staffServiceMock.getStaffById(5L)).thenReturn(staff);

        String simulatedInput = "1\n1\n5\n0\n0\n0\n";
        Scanner scanner = new Scanner(simulatedInput);

        adminMenu.menu(scanner);

        assertTrue(outContent.toString().contains("Editing cancelled."));
    }

    @Test
    @DisplayName("Should handle invalid option during edit staff info")
    void testEditStaffInfo_InvalidOption() {
        Staff staff = new Staff();
        staff.setId(5L);

        when(staffServiceMock.getAllStaff()).thenReturn(List.of(staff));
        when(staffServiceMock.getStaffById(5L)).thenReturn(staff);

        String simulatedInput = "1\n1\n5\n99\n0\n0\n";
        Scanner scanner = new Scanner(simulatedInput);

        adminMenu.menu(scanner);

        assertTrue(outContent.toString().contains("Invalid Option!"));
    }

    @Test
    @DisplayName("Should handle invalid ID format and entity not found during edit")
    void testHandleEditStaff_Errors() {
        Staff staff = new Staff();
        staff.setId(5L);
        when(staffServiceMock.getAllStaff()).thenReturn(List.of(staff));
        when(staffServiceMock.getStaffById(5L)).thenReturn(staff);
        when(staffServiceMock.getStaffById(100L)).thenThrow(new EntityNotFoundException("Not found"));

        // Flow:
        // 1 -> 1 -> "abc" -> "5" (valid ID to break loop) -> 0 (Cancel edit)
        // 1 -> 1 -> "100" (EntityNotFoundException) -> 0 -> 0
        String simulatedInput = "1\n1\nabc\n5\n0\n1\n100\n0\n0\n";
        Scanner scanner = new Scanner(simulatedInput);

        adminMenu.menu(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Invalid ID format! Please enter a valid number."));
        assertTrue(output.contains("Not found"));
    }

    @Test
    @DisplayName("Should successfully add a new staff member")
    void testHandleAddNewStaff_Success() {
        String simulatedInput = "2\nJohn\nDoe\njohn.doe@test.com\nsecretPass\n0\n";
        Scanner scanner = new Scanner(simulatedInput);

        doNothing().when(userServiceMock).validateEmailIsFree("john.doe@test.com");

        adminMenu.menu(scanner);

        verify(staffServiceMock, times(1)).addNewStaff(any(Staff.class));
        assertTrue(outContent.toString().contains("Staff was successfully added!"));
    }
}