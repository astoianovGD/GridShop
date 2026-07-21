package com.bobocode.Services.User;

import com.bobocode.Entities.Users.AbstractUser;
import com.bobocode.Entities.Users.User;
import com.bobocode.Exceptions.EmailAlreadyExistsException;
import com.bobocode.Exceptions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class UserService {

    private final Map<Long, AbstractUser> allUsers;

    public void registerNewUser(User newUser) {
        newUser.setId(AbstractUser.globalIdCounter++);
        allUsers.put(newUser.getId(), newUser);
    }

    public void deleteUserAccount(long id) {
        if (allUsers.get(id) instanceof User) {
            allUsers.remove(id);
        } else {
            throw new IllegalArgumentException("Entity with this ID is not a User!");
        }
    }

    public void editPersonalInformation(long id, User changedUser) {
        allUsers.put(id, changedUser);
    }

    public List<User> getAllUsers() {
        return allUsers.values().stream()
                .filter(User.class::isInstance)
                .map(User.class::cast)
                .toList();
    }

    public User getUserById(long id) {
        if (allUsers.get(id) instanceof User user) {
            return user;
        }
        throw new EntityNotFoundException("User with ID " + id + " not found!");
    }

    public boolean isEmailTaken(String email) {
        return allUsers.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    public void validateEmailIsFree(String email) {
        if (isEmailTaken(email)) {
            throw new EmailAlreadyExistsException("Error 409: This email is already registered! Please try another one.");
        }
    }
}