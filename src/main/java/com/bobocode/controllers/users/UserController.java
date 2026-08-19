package com.bobocode.controllers.users;

import com.bobocode.dto.users.UserDto;
import com.bobocode.dto.users.UserRegistrationDto;
import com.bobocode.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Retrieves all users.
     * GET /api/v1/users
     *
     * @return a list of user DTOs
     */
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Retrieves a specific user by their ID.
     * GET /api/v1/users/{id}
     *
     * @param id the ID of the user
     * @return the matching user DTO
     */
    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable long id) {
        return userService.getUserById(id);
    }

    /**
     * Registers a new user.
     * POST /api/v1/users
     *
     * @param userRegistrationDto the payload containing registration details
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // 201 Created
    public void createNewUser(@RequestBody UserRegistrationDto userRegistrationDto) {
        userService.registerNewUser(userRegistrationDto);
    }

    /**
     * Updates an existing user's personal information by ID.
     * PUT /api/v1/users/{id}
     *
     * @param id      the ID of the user to update
     * @param userDto the payload containing updated details
     */
    @PutMapping("/{id}")
    public void updateUser(@PathVariable long id, @RequestBody UserDto userDto) {
        userService.editPersonalInformation(id, userDto);
    }

    /**
     * Deletes a user account by ID.
     * DELETE /api/v1/users/{id}
     *
     * @param id the ID of the user to delete
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204 No Content
    public void deleteUser(@PathVariable long id) {
        userService.deleteUserAccount(id);
    }
}