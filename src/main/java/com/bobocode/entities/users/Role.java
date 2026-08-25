package com.bobocode.entities.users;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * Represents a user role.
 */
@Entity
@Data
@Table(name = "roles")
public class Role {

    /**
     * Unique identifier for the role.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    /**
     * The name of the role.
     */
    @NotNull
    @NotBlank
    @Size(max = 50)
    private String name;

    /**
     * The list of users associated with this role.
     */
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<User> users;
}
