package com.bobocode.dto.users;

import com.bobocode.enums.Gender;
import lombok.Data;

/**
 * Data Transfer Object representing a user.
 */
@Data
public class UserDto {
    /**
     * The unique identifier of the user.
     */
    private long id;

    /**
     * The email address of the user.
     */
    private String email;

    /**
     * The password of the user.
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
