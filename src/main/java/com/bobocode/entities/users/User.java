package com.bobocode.entities.users;

import com.bobocode.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * Represents a system user.
 */
@Data
@Entity
@Table(name = "users")
public final class User {

    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    /**
     * The email address of the user.
     */
    @NotBlank
    @NotNull
    @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Invalid email format")
    @Size(max = 100)
    private String email;

    /**
     * The password of the user.
     */
    @NotNull
    @NotBlank
    @Size(min = 8, max = 40)
    private String password;

    /**
     * The first name of the user.
     */
    @NotBlank
    @NotNull
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
    @NotNull
    @Min(0)
    @Max(150)
    private Integer age;

    /**
     * The gender of the user.
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    private Gender gender;

    /**
     * Indicates whether the user account is active.
     */
    @Column(name = "is_active")
    @NotNull
    private boolean isActive;

    /**
     * The role assigned to the user.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    @NotNull
    private Role role;
}
