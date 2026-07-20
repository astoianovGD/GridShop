package com.bobocode.Entities.Users;

import lombok.Data;

@Data
public abstract class AbstractUser {
    private long id;
    private String password;
    private String email;
    private String lastName;
    private String firstName;
    public static long globalIdCounter = 1;

    @Override
    public String toString() {
        return String.format("[%s] ID: %d | Name: %s %s | Email: %s",
                this.getClass().getSimpleName(), // Виведе Staff, User або Admin
                id,
                firstName,
                lastName,
                email);
    }
}
