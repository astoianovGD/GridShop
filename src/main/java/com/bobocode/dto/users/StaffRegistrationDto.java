package com.bobocode.dto.users;

import lombok.Data;

/**
 * Data Transfer Object for registering a staff member.
 */
@Data
public class StaffRegistrationDto {
    /**
     * The email address for registration.
     */
    private String email;

    /**
     * The password for registration.
     */
    private String password;

    /**
     * The first name of the staff member.
     */
    private String firstname;

    /**
     * The last name of the staff member.
     */
    private String lastname;
}
