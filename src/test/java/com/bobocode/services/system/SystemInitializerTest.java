package com.bobocode.services.system;

import com.bobocode.entities.users.Role;
import com.bobocode.entities.users.User;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.repositories.users.RoleRepository;
import com.bobocode.repositories.users.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SystemInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private SystemInitializer systemInitializer;

    @Test
    void shouldSkipInitializationWhenAdminAlreadyExists() {
        when(userRepository.existsByRoleName("ADMIN")).thenReturn(true);

        Scanner scanner = new Scanner(System.in);

        systemInitializer.initializeSystem(scanner);

        verify(userRepository).existsByRoleName("ADMIN");
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldCreateFirstAdminSuccessfullyWhenAdminDoesNotExist() {
        when(userRepository.existsByRoleName("ADMIN")).thenReturn(false);

        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(adminRole));

        // Input sequence for console validation:
        // First Name: "Alex"
        // Last Name: "Stoianov"
        // Email: "admin@test.com"
        // Password: "StrongPassword1!"
        String input = "Alex\nStoianov\nadmin@test.com\nStrongPassword1!\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        systemInitializer.initializeSystem(scanner);

        verify(userRepository).existsByRoleName("ADMIN");
        verify(roleRepository).findByName("ADMIN");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowEntityNotFoundWhenAdminRoleNotFoundInDatabase() {
        when(userRepository.existsByRoleName("ADMIN")).thenReturn(false);
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.empty());

        String input = "Alex\nStoianov\nadmin@test.com\nStrongPassword1!\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        assertThrows(EntityNotFoundException.class, () -> systemInitializer.initializeSystem(scanner));
        verify(userRepository, never()).save(any());
    }
}