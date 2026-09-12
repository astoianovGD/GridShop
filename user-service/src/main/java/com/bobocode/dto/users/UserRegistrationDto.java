package com.bobocode.dto.users;

import com.bobocode.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * Data Transfer Object for registering a user.
 */
@Data
public class UserRegistrationDto {
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
     * The first name of the user.
     */
    @NotNull
    @NotBlank
    @Size(max = 50)
    private String firstname;

    /**
     * The last name of the user.
     */
    @NotNull
    @NotBlank
    @Size(max = 50)
    private String lastname;

    /**
     * The age of the user.
     */
    @NotNull
    @Min(0)
    @Max(150)
    private Integer age;

    /**
     * The gender of the user.
     */
    @NotNull
    private Gender gender;
}
