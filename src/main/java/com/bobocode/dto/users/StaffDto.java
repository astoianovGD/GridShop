package com.bobocode.dto.users;

import lombok.Data;

/**
 * Data Transfer Object representing staff members.
 */
@Data
public class StaffDto {
    /**
     * The unique identifier of the staff member.
     */
    private long id;

    /**
     * The first name of the staff member.
     */
    private String firstname;

    /**
     * The last name of the staff member.
     */
    private String lastname;

    /**
     * The email address of the staff member.
     */
    private String email;

    /**
     * The password of the staff member.
     */
    private String password;
}
