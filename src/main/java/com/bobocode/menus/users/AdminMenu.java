package com.bobocode.menus.users;

import com.bobocode.dto.users.StaffRegistrationDto;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.services.user.StaffService;
import com.bobocode.services.user.StaffViewService;
import com.bobocode.services.user.UserService;
import com.bobocode.utility.EmailValidator;
import com.bobocode.utility.InputValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Scanner;

/**
 * Menu for handling administrator operations.
 */
@RequiredArgsConstructor
@Component
public final class AdminMenu {

    /**
     * Service for managing staff members.
     */
    @NonNull
    private final StaffService staffService;

    /**
     * Service for managing users.
     */
    @NonNull
    private final UserService userService;

    @NonNull
    private final StaffViewService staffViewService;

    /**
     * Displays the admin menu and handles user input.
     *
     * @param scanner the scanner for reading user input
     */
    public void menu(final Scanner scanner) {
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1) View All Staff \n2) Add New Staff "
                    + "\n0) Sign Out");
            switch (scanner.nextLine()) {
                case "1" -> handleManageStaff(scanner);
                case "2" -> handleAddNewStaff(scanner);
                case "0" -> {
                    System.out.println("Signing out...");
                    return;
                }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    /**
     * Handles viewing and managing existing staff members.
     *
     * @param scanner the scanner for reading user input
     */
    private void handleManageStaff(final Scanner scanner) {
        var staffList = staffService.getAllStaff();
        if (staffList.isEmpty()) {
            System.out.println("Staff list is empty! Add someone first.");
            return;
        }

        System.out.println("--- ALL STAFF --- ");
        staffList.forEach(staffViewService::printStaffDetails);
        //print change

        while (true) {
            System.out.println("\nWanna do smth else?");
            System.out.println("1) Edit Staff \n2) Delete Staff "
                    + "\n0) Nothing");
            String option = scanner.nextLine();

            if ("0".equals(option)) {
                break;
            }

            switch (option) {
                case "1" -> handleEditStaff(scanner);
                case "2" -> handleDeleteStaff(scanner);
                default -> System.out.println("Invalid option!");
            }
        }
    }

    /**
     * Handles the process of editing a staff member.
     *
     * @param scanner the scanner for reading user input
     */
    private void handleEditStaff(final Scanner scanner) {
        long id = InputValidator.getValidId(
                scanner, "Enter ID of Staff to edit:"
        );
        try {
            // Перевіряємо чи існує такий staff перед викликом меню редагування
            staffService.getStaffById(id);
            editStaffInfo(scanner, id);
        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Handles the process of deleting a staff member.
     *
     * @param scanner the scanner for reading user input
     */
    private void handleDeleteStaff(final Scanner scanner) {
        long idToDelete = InputValidator.getValidId(
                scanner, "Enter ID of Staff to delete:"
        );
        try {
            staffService.removeStaff(idToDelete);
            System.out.println("Staff successfully deleted!");
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Handles the creation of a new staff member.
     *
     * @param scanner the scanner for reading user input
     */
    private void handleAddNewStaff(final Scanner scanner) {
        System.out.println("---- ADD NEW STAFF ----");
        StaffRegistrationDto newStaff = new StaffRegistrationDto();

        newStaff.setFirstname(InputValidator.getValidName(
                scanner, "First Name")
        );

        newStaff.setLastname(InputValidator.getValidName(
                scanner, "Last Name")
        );

        String email = EmailValidator.getUniqueEmailFromConsole(
                scanner, userService);
        newStaff.setEmail(email);

        newStaff.setPassword(InputValidator.getValidPassword(scanner));

        staffService.addNewStaff(newStaff);
        System.out.println("Staff was successfully added!");
    }

    /**
     * Helper method to edit specific details of a staff member.
     *
     * @param scanner the scanner for reading user input
     * @param staffId the ID of the staff member being edited
     */
    private void editStaffInfo(final Scanner scanner, final long staffId) {
        System.out.println("\n--- Edit Staff ---");
        System.out.println("1) First Name \n2) Last Name \n3) Email "
                + "\n4) Password \n0) Cancel");
        String option = scanner.nextLine();

        switch (option) {
            case "1" -> {
                String newFirstName = InputValidator.getValidName(scanner, "First Name");
                staffService.updateStaffField(staffId, u -> u.setFirstname(newFirstName));
                System.out.println("First Name successfully updated!");
            }
            case "2" -> {
                String newLastName = InputValidator.getValidName(scanner, "Last Name");
                staffService.updateStaffField(staffId, u -> u.setLastname(newLastName));
                System.out.println("Last Name successfully updated!");
            }
            case "3" -> {
                String newEmail = EmailValidator.getUniqueEmailFromConsole(scanner, userService);
                staffService.updateStaffField(staffId, u -> u.setEmail(newEmail));
                System.out.println("Email successfully updated!");
            }
            case "4" -> {
                String newPassword = InputValidator.getValidPassword(scanner);
                staffService.updateStaffField(staffId, u -> u.setPassword(newPassword));
                System.out.println("Password successfully updated!");
            }
            case "0" -> {
                System.out.println("Editing cancelled.");
            }
            default -> {
                System.out.println("Invalid Option!");
            }
        }
    }
}