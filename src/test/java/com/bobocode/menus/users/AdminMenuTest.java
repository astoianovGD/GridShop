package com.bobocode.menus.users;

import com.bobocode.dto.users.StaffDto;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.services.user.StaffService;
import com.bobocode.services.user.StaffViewService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminMenuTest {

    @Mock
    private StaffService staffService;

    @Mock
    private UserService userService;

    @Mock
    private StaffViewService staffViewService;

    @InjectMocks
    private AdminMenu adminMenu;

    @Test
    void shouldExitAdminMenuSuccessfully() {
        Scanner scanner = createScanner("0\n");

        adminMenu.menu(scanner);

        verifyNoInteractions(staffService, userService, staffViewService);
    }

    @Test
    void shouldHandleInvalidMenuOptionThenExit() {
        Scanner scanner = createScanner("invalid\n0\n");

        adminMenu.menu(scanner);

        verifyNoInteractions(staffService, userService, staffViewService);
    }

    @Test
    void shouldHandleEmptyStaffListWhenManaging() {
        when(staffService.getAllStaff()).thenReturn(Collections.emptyList());

        Scanner scanner = createScanner("1\n0\n");

        adminMenu.menu(scanner);

        verify(staffService).getAllStaff();
        verifyNoInteractions(staffViewService);
    }

    @Test
    void shouldHandleStaffListAndExitSubmenu() {
        StaffDto staff = new StaffDto();
        when(staffService.getAllStaff()).thenReturn(List.of(staff));

        Scanner scanner = createScanner("1\n0\n0\n");

        adminMenu.menu(scanner);

        verify(staffService).getAllStaff();
        verify(staffViewService).printStaffDetails(staff);
    }

    @Test
    void shouldDeleteStaffSuccessfully() {
        StaffDto staff = new StaffDto();
        when(staffService.getAllStaff()).thenReturn(List.of(staff));

        Scanner scanner = createScanner("1\n2\n5\n0\n0\n");

        adminMenu.menu(scanner);

        verify(staffService).removeStaff(5L);
    }

    @Test
    void shouldHandleEntityNotFoundDuringDeletion() {
        StaffDto staff = new StaffDto();
        when(staffService.getAllStaff()).thenReturn(List.of(staff));
        doThrow(new EntityNotFoundException("Staff not found")).when(staffService).removeStaff(99L);

        Scanner scanner = createScanner("1\n2\n99\n0\n0\n");

        adminMenu.menu(scanner);

        verify(staffService).removeStaff(99L);
    }

    @Test
    void shouldEditStaffFirstNameSuccessfully() {
        StaffDto staff = new StaffDto();
        when(staffService.getAllStaff()).thenReturn(List.of(staff));
        when(staffService.getStaffById(1L)).thenReturn(staff);

        Scanner scanner = createScanner("1\n1\n1\n1\nJohn\n0\n0\n");

        adminMenu.menu(scanner);

        verify(staffService).getStaffById(1L);
        verify(staffService).updateStaffField(eq(1L), any());
    }

    @Test
    void shouldHandleEntityNotFoundDuringEdit() {
        StaffDto staff = new StaffDto();
        when(staffService.getAllStaff()).thenReturn(List.of(staff));
        doThrow(new EntityNotFoundException("Staff not found")).when(staffService).getStaffById(1L);

        Scanner scanner = createScanner("1\n1\n1\n0\n0\n");

        adminMenu.menu(scanner);

        verify(staffService).getStaffById(1L);
        verify(staffService, never()).updateStaffField(anyLong(), any());
    }

    @Test
    void shouldCancelStaffEdit() {
        StaffDto staff = new StaffDto();
        when(staffService.getAllStaff()).thenReturn(List.of(staff));
        when(staffService.getStaffById(1L)).thenReturn(staff);

        Scanner scanner = createScanner("1\n1\n1\n0\n0\n0\n");

        adminMenu.menu(scanner);

        verify(staffService, never()).updateStaffField(anyLong(), any());
    }

    @Test
    void shouldAddNewStaffSuccessfully() {
        Scanner scanner = createScanner("2\nAlex\nStoianov\nalex@test.com\nStrongPass1!\n0\n");

        adminMenu.menu(scanner);

        verify(staffService).addNewStaff(any());
    }

    private Scanner createScanner(String input) {
        return new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
    }
}