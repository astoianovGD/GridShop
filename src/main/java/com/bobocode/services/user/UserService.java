package com.bobocode.services.user;

import com.bobocode.dto.users.UserDto;
import com.bobocode.dto.users.UserRegistrationDto;
import com.bobocode.entities.users.Role;
import com.bobocode.entities.users.User;
import com.bobocode.exceptions.EmailAlreadyExistsException;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.mappers.users.UserRegistrationMapper;
import com.bobocode.mappers.users.UserMapper;
import com.bobocode.repositories.users.RoleRepository;
import com.bobocode.repositories.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Consumer;

/**
 * Service class for managing standard users.
 */
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final UserRegistrationMapper userRegistrationMapper;

    private final RoleRepository roleRepository;
    /**
     * Registers a new user in the system.
     *
     * @param newUser the user to register
     */
    @Transactional
    public void registerNewUser(final UserRegistrationDto newUser) {
        User user = userRegistrationMapper.toEntity(newUser);

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new EntityNotFoundException("Default role 'USER' not found!"));

        user.setRole(userRole);

        userRepository.save(user);
    }

    /**
     * Deletes a user account by its ID.
     *
     * @param userId the ID of the user to delete
     */
    @Transactional
    public void deleteUserAccount(final long userId) {
        User user = userRepository
                .findUserByIdAndRoleNameAndIsActive(
                        userId,
                        "USER",
                        true
                ).orElseThrow(() -> new EntityNotFoundException(
                        "User with ID " + userId + " not found!"
                ));

        user.setActive(false);
        userRepository.save(user);
    }

    /**
     * Updates the personal information of an existing user.
     *
     * @param userId          the ID of the user to update
     * @param userDto the user object containing updated information
     */
    @Transactional
    public void editPersonalInformation(
            final long userId, final UserDto userDto) {
        User existingUser = userRepository
                .findUserByIdAndRoleNameAndIsActive(
                        userId,
                        "USER",
                        true
                ).orElseThrow(() -> new EntityNotFoundException(
                        "User with ID " + userId + " not found!"
                ));

        existingUser.setFirstname(userDto.getFirstname());
        existingUser.setLastname(userDto.getLastname());
        existingUser.setAge(userDto.getAge());
        existingUser.setGender(userDto.getGender());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setPassword(userDto.getPassword());

        userRepository.save(existingUser);
    }

    /**
     * Retrieves a list of all registered users.
     *
     * @return a list containing all users as DTOs
     */
    public List<UserDto> getAllUsers() {
        return userRepository.findAllByRoleNameAndIsActive("USER", true)
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    /**
     * Retrieves a specific user by their ID.
     *
     * @param userId the ID of the user to retrieve
     * @return the requested user as a DTO
     * @throws EntityNotFoundException if the user is not found
     */
    public UserDto getUserById(final long userId) {
        User user = userRepository
                .findUserByIdAndRoleNameAndIsActive(
                        userId,
                        "USER",
                        true
                ).orElseThrow(() -> new EntityNotFoundException(
                "User with ID " + userId + " not found!"
        ));
        return userMapper.toDto(user);
    }

    /**
     * Checks if a given email is already taken.
     *
     * @param email the email to check
     * @return true if the email is registered, false otherwise
     */
    public boolean isEmailTaken(final String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Validates that the provided email is not already registered.
     *
     * @param email the email to validate
     * @throws EmailAlreadyExistsException if the email is already in use
     */
    public void validateEmailIsFree(final String email) {
        if (isEmailTaken(email)) {
            throw new EmailAlreadyExistsException(
                    "Error 409: This email is already registered! "
                            + "Please try another one."
            );
        }
    }

    /**
     * Universal method to update specific fields of a user within a transaction.
     *
     * @param userId       the ID of the user to update
     * @param fieldUpdater a lambda or method reference representing the field update
     */
    @Transactional
    public void updateUserField(final long userId, final Consumer<User> fieldUpdater) {
        User existingUser = userRepository
                .findUserByIdAndRoleNameAndIsActive(userId, "USER", true)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User with ID " + userId + " not found!"
                ));

        // do lyambda function which we get
        fieldUpdater.accept(existingUser);

        userRepository.save(existingUser);
    }

}
