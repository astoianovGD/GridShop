package com.bobocode.entities.users;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
    @Column(name = "role_id")
    private long id;

    /**
     * The name of the role.
     */
    private String name;

    /**
     * The list of users associated with this role.
     */
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<User> users;
}
