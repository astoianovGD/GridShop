package com.bobocode.entities.users;

import com.bobocode.enums.Gender;
import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "users")
public final class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long id;

    private String email;

    private String password;

    private String firstname;

    private String lastname;

    private int age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "is_active")
    private boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
}
