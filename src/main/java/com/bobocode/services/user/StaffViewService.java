package com.bobocode.services.user;

import com.bobocode.dto.users.StaffDto;
import org.springframework.stereotype.Service;

@Service
public class StaffViewService {

    /**
     * Prints the staff member's details nicely to the console, omitting sensitive data like passwords.
     *
     * @code StaffDto the staff DTO to display
     */
    public void printStaffDetails(StaffDto staff) {
        if (staff == null) {
            System.out.println("No staff data available.");
            return;
        }

        System.out.println("========================================");
        System.out.println("            STAFF INFORMATION           ");
        System.out.println("========================================");
        System.out.printf(" ID        : %d%n", staff.getId());
        System.out.printf(" First Name: %s%n", staff.getFirstname());
        System.out.printf(" Last Name : %s%n", staff.getLastname());
        System.out.printf(" Email     : %s%n", staff.getEmail());
        System.out.println("========================================");
    }
}