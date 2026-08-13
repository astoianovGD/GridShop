package com.bobocode.dto.users;

import com.bobocode.enums.Gender;
import lombok.Data;

/**
 * Data Transfer Object for registering a user.
 */
@Data
public class UserRegistrationDto {
    /**
     * The email address for registration.
     */
    private String email;

    /**
     * The password for registration.
     */
    private String password;

    /**
     * The first name of the user.
     */
    private String firstname;

    /**
     * The last name of the user.
     */
    private String lastname;

    /**
     * The age of the user.
     */
    private int age;

    /**
     * The gender of the user.
     */
    private Gender gender;
}
