package com.bobocode.Services.User;

import com.bobocode.Entities.Users.AbstractUser;
import com.bobocode.Exceptions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class AuthService {
    private final Map<Long, AbstractUser> allUsers;

    public AbstractUser signIn(String email, String password) {
        System.out.println("Searching in base...");
        return allUsers.values().stream()
                .filter(user -> user.getEmail().equals(email) && user.getPassword().equals(password))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Error: Invalid email or password! Please try again."));
    }

}
