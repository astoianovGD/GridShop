package com.bobocode.dto.users;

import com.bobocode.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * Data Transfer Object representing a user.
 */
@Data
public class UserDto {
    /**
     * The unique identifier of the user.
     */
    @NotNull
    private Long id;

    /**
     * The email address of the user.
     */
    @NotNull
    @NotBlank
    @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Invalid email format")
    @Size(max = 50)
    private String email;

    /**
     * The password of the user.
     */
    @NotBlank
    @NotNull
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
    @NotBlank
    @NotNull
    @Size(max = 50)
    private String lastname;

    /**
     * The age of the user.
     */
    @Min(0)
    @Max(150)
    private Integer age;

    /**
     * The gender of the user.
     */
    @NotNull
    private Gender gender;
}
