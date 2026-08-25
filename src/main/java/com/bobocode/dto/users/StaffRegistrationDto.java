package com.bobocode.dto.users;

import com.bobocode.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * Data Transfer Object for registering a staff member.
 */
@Data
public class StaffRegistrationDto {
    /**
     * The email address for registration.
     */
    @NotNull
    @NotBlank
    @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Invalid email format")
    @Size(max = 100)
    private String email;

    /**
     * The password for registration.
     */
    @NotNull
    @NotBlank
    @Size(min = 8, max = 40)
    private String password;

    /**
     * The first name of the staff member.
     */
    @NotBlank
    @NotNull
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
     * The age of the staff member.
     */
    @NotNull
    @Min(0)
    @Max(150)
    private Integer age;

    /**
     * The gender of the staff member.
     */
    @NotNull
    private Gender gender;
}
