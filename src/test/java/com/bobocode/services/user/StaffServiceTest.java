package com.bobocode.services.user;

import com.bobocode.dto.users.StaffDto;
import com.bobocode.dto.users.StaffRegistrationDto;
import com.bobocode.entities.users.Role;
import com.bobocode.entities.users.User;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.mappers.users.StaffMapper;
import com.bobocode.mappers.users.StaffRegistrationMapper;
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
public class StaffServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private StaffMapper staffMapper;
    @Mock private StaffRegistrationMapper staffRegistrationMapper;
    @Mock private RoleRepository roleRepository;

    @InjectMocks
    private StaffService staffService;

    @Test
    void shouldAddNewStaffSuccessfully() {
        StaffRegistrationDto regDto = new StaffRegistrationDto();
        User user = new User();
        Role staffRole = new Role();
        staffRole.setName("STAFF");

        when(staffRegistrationMapper.toEntity(regDto)).thenReturn(user);
        when(roleRepository.findByName("STAFF")).thenReturn(Optional.of(staffRole));

        staffService.addNewStaff(regDto);

        assertEquals(staffRole, user.getRole());
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowEntityNotFoundWhenStaffRoleNotFoundOnAdd() {
        StaffRegistrationDto regDto = new StaffRegistrationDto();
        User user = new User();

        when(staffRegistrationMapper.toEntity(regDto)).thenReturn(user);
        when(roleRepository.findByName("STAFF")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> staffService.addNewStaff(regDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldEditStaffSuccessfully() {
        StaffDto staffDto = new StaffDto();
        staffDto.setFirstname("John");
        staffDto.setLastname("Doe");
        staffDto.setEmail("john@staff.com");
        staffDto.setPassword("pass1234");

        User existingStaff = new User();
        existingStaff.setId(1L);

        when(userRepository.findUserByIdAndRoleNameAndIsActive(1L, "STAFF", true))
                .thenReturn(Optional.of(existingStaff));

        staffService.editStaff(1L, staffDto);

        assertEquals("John", existingStaff.getFirstname());
        assertEquals("Doe", existingStaff.getLastname());
        assertEquals("john@staff.com", existingStaff.getEmail());
        assertEquals("pass1234", existingStaff.getPassword());
        verify(userRepository).save(existingStaff);
    }

    @Test
    void shouldThrowEntityNotFoundWhenStaffNotFoundOnEdit() {
        StaffDto staffDto = new StaffDto();

        when(userRepository.findUserByIdAndRoleNameAndIsActive(99L, "STAFF", true))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> staffService.editStaff(99L, staffDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldRemoveStaffSuccessfully() {
        User user = new User();
        user.setId(1L);
        user.setActive(true);

        when(userRepository.findUserByIdAndRoleNameAndIsActive(1L, "STAFF", true))
                .thenReturn(Optional.of(user));

        staffService.removeStaff(1L);

        assertFalse(user.isActive());
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowEntityNotFoundWhenStaffNotFoundOnRemoval() {
        when(userRepository.findUserByIdAndRoleNameAndIsActive(99L, "STAFF", true))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> staffService.removeStaff(99L));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldGetAllStaffSuccessfully() {
        User user = new User();
        StaffDto dto = new StaffDto();

        when(userRepository.findAllByRoleNameAndIsActive("STAFF", true)).thenReturn(List.of(user));
        when(staffMapper.toDto(user)).thenReturn(dto);

        List<StaffDto> result = staffService.getAllStaff();

        assertEquals(1, result.size());
        verify(userRepository).findAllByRoleNameAndIsActive("STAFF", true);
    }

    @Test
    void shouldReturnEmptyListWhenNoStaffFound() {
        when(userRepository.findAllByRoleNameAndIsActive("STAFF", true)).thenReturn(Collections.emptyList());

        List<StaffDto> result = staffService.getAllStaff();

        assertTrue(result.isEmpty());
        verifyNoInteractions(staffMapper);
    }

    @Test
    void shouldGetStaffByIdSuccessfully() {
        User user = new User();
        user.setId(1L);
        StaffDto dto = new StaffDto();

        when(userRepository.findUserByIdAndRoleNameAndIsActive(1L, "STAFF", true))
                .thenReturn(Optional.of(user));
        when(staffMapper.toDto(user)).thenReturn(dto);

        StaffDto result = staffService.getStaffById(1L);

        assertNotNull(result);
        verify(userRepository).findUserByIdAndRoleNameAndIsActive(1L, "STAFF", true);
    }

    @Test
    void shouldThrowEntityNotFoundWhenStaffNotFoundById() {
        when(userRepository.findUserByIdAndRoleNameAndIsActive(99L, "STAFF", true))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> staffService.getStaffById(99L));
    }

    @Test
    void shouldUpdateStaffFieldSuccessfully() {
        User user = new User();
        user.setId(1L);
        user.setFirstname("OldName");

        when(userRepository.findUserByIdAndRoleNameAndIsActive(1L, "STAFF", true))
                .thenReturn(Optional.of(user));

        staffService.updateStaffField(1L, u -> u.setFirstname("NewName"));

        assertEquals("NewName", user.getFirstname());
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowEntityNotFoundWhenStaffNotFoundOnFieldUpdate() {
        when(userRepository.findUserByIdAndRoleNameAndIsActive(99L, "STAFF", true))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> staffService.updateStaffField(99L, u -> u.setFirstname("Test")));
        verify(userRepository, never()).save(any());
    }
}