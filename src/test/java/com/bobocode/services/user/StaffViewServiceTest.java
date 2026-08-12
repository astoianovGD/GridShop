package com.bobocode.services.user;

import com.bobocode.dto.users.StaffDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class StaffViewServiceTest {

    private final StaffViewService staffViewService = new StaffViewService();

    @Test
    void shouldDisplayNullStaffMessageWhenStaffIsNull() {
        assertDoesNotThrow(() -> staffViewService.printStaffDetails(null));
    }

    @Test
    void shouldPrintStaffDetailsSuccessfully() {
        StaffDto staff = new StaffDto();
        staff.setId(1L);
        staff.setFirstname("Alex");
        staff.setLastname("Stoianov");
        staff.setEmail("alex.staff@test.com");

        assertDoesNotThrow(() -> staffViewService.printStaffDetails(staff));
    }
}