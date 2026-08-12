package com.bobocode.services.system;

import com.bobocode.entities.users.Role;
import com.bobocode.entities.users.User;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.repositories.users.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldSignInSuccessfully() {
        String email = "test@example.com";
        String password = "password123";

        Role role = new Role();
        role.setName("USER");

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = authService.signIn(email, password);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals("USER", result.getRole().getName());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void shouldThrowEntityNotFoundWhenEmailNotFound() {
        String email = "unknown@example.com";
        String password = "password123";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> authService.signIn(email, password));
        verify(userRepository).findByEmail(email);
    }

    @Test
    void shouldThrowEntityNotFoundWhenPasswordDoesNotMatch() {
        String email = "test@example.com";
        String correctPassword = "password123";
        String wrongPassword = "wrongPassword";

        User user = new User();
        user.setEmail(email);
        user.setPassword(correctPassword);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertThrows(EntityNotFoundException.class, () -> authService.signIn(email, wrongPassword));
        verify(userRepository).findByEmail(email);
    }
}