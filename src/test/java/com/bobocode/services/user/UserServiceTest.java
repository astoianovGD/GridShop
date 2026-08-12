package com.bobocode.services.user;

import com.bobocode.dto.users.UserDto;
import com.bobocode.dto.users.UserRegistrationDto;
import com.bobocode.entities.users.Role;
import com.bobocode.entities.users.User;
import com.bobocode.exceptions.EmailAlreadyExistsException;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.mappers.users.UserMapper;
import com.bobocode.mappers.users.UserRegistrationMapper;
import com.bobocode.repositories.users.RoleRepository;
import com.bobocode.repositories.users.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private UserRegistrationMapper userRegistrationMapper;
    @Mock private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldRegisterNewUserSuccessfully() {
        UserRegistrationDto regDto = new UserRegistrationDto();
        User user = new User();
        Role userRole = new Role();
        userRole.setName("USER");

        when(userRegistrationMapper.toEntity(regDto)).thenReturn(user);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));

        userService.registerNewUser(regDto);

        assertEquals(userRole, user.getRole());
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowEntityNotFoundWhenUserRoleNotFoundOnRegistration() {
        UserRegistrationDto regDto = new UserRegistrationDto();
        User user = new User();

        when(userRegistrationMapper.toEntity(regDto)).thenReturn(user);
        when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.registerNewUser(regDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldDeleteUserAccountSuccessfully() {
        User user = new User();
        user.setId(1L);
        user.setActive(true);

        when(userRepository.findUserByIdAndRoleNameAndIsActive(1L, "USER", true))
                .thenReturn(Optional.of(user));

        userService.deleteUserAccount(1L);

        assertFalse(user.isActive());
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowEntityNotFoundWhenUserNotFoundOnDeletion() {
        when(userRepository.findUserByIdAndRoleNameAndIsActive(99L, "USER", true))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.deleteUserAccount(99L));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldEditPersonalInformationSuccessfully() {
        UserDto dto = new UserDto();
        dto.setFirstname("Alex");
        dto.setLastname("Stoianov");
        dto.setAge(20);
        dto.setEmail("alex@test.com");
        dto.setPassword("pass1234");

        User existingUser = new User();
        existingUser.setId(1L);

        when(userRepository.findUserByIdAndRoleNameAndIsActive(1L, "USER", true))
                .thenReturn(Optional.of(existingUser));

        userService.editPersonalInformation(1L, dto);

        assertEquals("Alex", existingUser.getFirstname());
        assertEquals("Stoianov", existingUser.getLastname());
        assertEquals(20, existingUser.getAge());
        assertEquals("alex@test.com", existingUser.getEmail());
        assertEquals("pass1234", existingUser.getPassword());
        verify(userRepository).save(existingUser);
    }

    @Test
    void shouldThrowEntityNotFoundWhenUserNotFoundOnEditPersonalInformation() {
        UserDto dto = new UserDto();

        when(userRepository.findUserByIdAndRoleNameAndIsActive(99L, "USER", true))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.editPersonalInformation(99L, dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldGetAllUsersSuccessfully() {
        User user = new User();
        UserDto dto = new UserDto();

        when(userRepository.findAllByRoleNameAndIsActive("USER", true)).thenReturn(List.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        List<UserDto> result = userService.getAllUsers();

        assertEquals(1, result.size());
        verify(userRepository).findAllByRoleNameAndIsActive("USER", true);
    }

    @Test
    void shouldReturnEmptyListWhenNoUsersFound() {
        when(userRepository.findAllByRoleNameAndIsActive("USER", true)).thenReturn(Collections.emptyList());

        List<UserDto> result = userService.getAllUsers();

        assertTrue(result.isEmpty());
        verifyNoInteractions(userMapper);
    }

    @Test
    void shouldGetUserByIdSuccessfully() {
        User user = new User();
        user.setId(1L);
        UserDto dto = new UserDto();

        when(userRepository.findUserByIdAndRoleNameAndIsActive(1L, "USER", true))
                .thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        UserDto result = userService.getUserById(1L);

        assertNotNull(result);
        verify(userRepository).findUserByIdAndRoleNameAndIsActive(1L, "USER", true);
    }

    @Test
    void shouldThrowEntityNotFoundWhenUserNotFoundById() {
        when(userRepository.findUserByIdAndRoleNameAndIsActive(99L, "USER", true))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    void shouldCheckIsEmailTaken() {
        when(userRepository.existsByEmail("taken@test.com")).thenReturn(true);
        when(userRepository.existsByEmail("free@test.com")).thenReturn(false);

        assertTrue(userService.isEmailTaken("taken@test.com"));
        assertFalse(userService.isEmailTaken("free@test.com"));
    }

    @Test
    void shouldValidateEmailIsFreeSuccessfully() {
        when(userRepository.existsByEmail("free@test.com")).thenReturn(false);

        assertDoesNotThrow(() -> userService.validateEmailIsFree("free@test.com"));
    }

    @Test
    void shouldThrowEmailAlreadyExistsWhenEmailIsTaken() {
        when(userRepository.existsByEmail("taken@test.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.validateEmailIsFree("taken@test.com"));
    }

    @Test
    void shouldUpdateUserFieldSuccessfully() {
        User user = new User();
        user.setId(1L);
        user.setFirstname("OldName");

        when(userRepository.findUserByIdAndRoleNameAndIsActive(1L, "USER", true))
                .thenReturn(Optional.of(user));

        userService.updateUserField(1L, u -> u.setFirstname("NewName"));

        assertEquals("NewName", user.getFirstname());
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowEntityNotFoundWhenUserNotFoundOnFieldUpdate() {
        when(userRepository.findUserByIdAndRoleNameAndIsActive(99L, "USER", true))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updateUserField(99L, u -> u.setFirstname("Test")));
        verify(userRepository, never()).save(any());
    }
}