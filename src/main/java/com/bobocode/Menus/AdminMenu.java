package com.bobocode.Menus;

import com.bobocode.Entities.Users.Staff;
import com.bobocode.Exceptions.EntityNotFoundException;
import com.bobocode.Services.User.StaffService;
import com.bobocode.Services.User.UserService;
import com.bobocode.Utility.EmailValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Scanner;

/**
 * Menu for handling administrator operations.
 */
@RequiredArgsConstructor
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
        staffList.forEach(System.out::println);

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
        System.out.println("Enter ID of Staff to edit:");
        try {
            long id = Long.parseLong(scanner.nextLine());
            Staff staff = staffService.getStaffById(id);
            editStaffInfo(scanner, staff);
        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format! Please enter a number.");
        }
    }

    /**
     * Handles the process of deleting a staff member.
     *
     * @param scanner the scanner for reading user input
     */
    private void handleDeleteStaff(final Scanner scanner) {
        System.out.println("Enter ID of Staff to delete:");
        try {
            long idToDelete = Long.parseLong(scanner.nextLine());
            staffService.removeStaff(idToDelete);
            System.out.println("Staff successfully deleted!");
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format! Please enter a number.");
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
        Staff newStaff = new Staff();

        System.out.println("Enter First Name: ");
        newStaff.setFirstName(scanner.nextLine());

        System.out.println("Enter Last Name: ");
        newStaff.setLastName(scanner.nextLine());

        String email = EmailValidator.getUniqueEmailFromConsole(
                scanner, userService);
        newStaff.setEmail(email);

        System.out.println("Enter Password: ");
        newStaff.setPassword(scanner.nextLine());

        staffService.addNewStaff(newStaff);
        System.out.println("Staff was successfully added!");
    }

    /**
     * Helper method to edit specific details of a staff member.
     *
     * @param scanner the scanner for reading user input
     * @param staff   the staff member being edited
     */
    private void editStaffInfo(final Scanner scanner, final Staff staff) {
        System.out.println("\n--- Edit Staff ---");
        System.out.println("1) First Name \n2) Last Name \n3) Email "
                + "\n4) Password \n0) Cancel");
        String option = scanner.nextLine();

        switch (option) {
            case "1" -> {
                System.out.println("Enter new First Name:");
                staff.setFirstName(scanner.nextLine());
            }
            case "2" -> {
                System.out.println("Enter new Last Name:");
                staff.setLastName(scanner.nextLine());
            }
            case "3" -> {
                String email = EmailValidator.getUniqueEmailFromConsole(
                        scanner, userService);
                staff.setEmail(email);
                System.out.println("Email updated!");
            }
            case "4" -> {
                System.out.println("Enter new Password ");
                staff.setPassword(scanner.nextLine());
            }
            case "0" -> {
                System.out.println("Editing cancelled.");
                return;
            }
            default -> {
                System.out.println("Invalid Option!");
                return;
            }
        }

        staffService.editStaff(staff.getId(), staff);
        System.out.println("Staff profile updated!");
    }
}
