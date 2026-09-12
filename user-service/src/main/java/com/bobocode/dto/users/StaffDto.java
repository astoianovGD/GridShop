package com.bobocode.dto.users;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * Data Transfer Object representing staff members.
 */
@Data
public class StaffDto {
    /**
     * The unique identifier of the staff member.
     */
    @NotNull
    private Long id;

    /**
     * The first name of the staff member.
     */
    @NotNull
    @NotBlank
    @Size(max = 50)
    private String firstname;

    /**
     * The last name of the staff member.
     */
    @NotNull
    @NotBlank
    @Size(max = 50)
    private String lastname;

    /**
     * The email address of the staff member.
     */
    @NotNull
    @NotBlank
    @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Invalid email format")
    @Size(max = 100)
    private String email;

    /**
     * The password of the staff member.
     */
    @NotNull
    @NotBlank
    @Size(min = 8, max = 40)
    private String password;
}
