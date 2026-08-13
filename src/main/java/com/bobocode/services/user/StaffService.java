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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Consumer;

/**
 * Service for managing staff members.
 */
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class StaffService {

    /**
     * Repository for managing users.
     */
    private final UserRepository userRepository;

    /**
     * Mapper for staff DTOs.
     */
    private final StaffMapper staffMapper;

    /**
     * Mapper for staff registration.
     */
    private final StaffRegistrationMapper staffRegistrationMapper;

    /**
     * Repository for managing roles.
     */
    private final RoleRepository roleRepository;

    /**
     * Adds a new staff member.
     *
     * @param newStaff the staff member to add
     */
    @Transactional
    public void addNewStaff(final StaffRegistrationDto newStaff) {
        User user = staffRegistrationMapper.toEntity(newStaff);

        Role role = roleRepository.findByName("STAFF")
                .orElseThrow(() -> new EntityNotFoundException(
                        "Default role 'STAFF' not found!"
                ));

        user.setRole(role);

        userRepository.save(user);
    }

    /**
     * Edits an existing staff member.
     *
     * @param staffId  the ID of the staff member to edit
     * @param staffDto the updated staff member details
     */
    @Transactional
    public void editStaff(final long staffId, final StaffDto staffDto) {
        User existingStaff = userRepository
                .findUserByIdAndRoleNameAndIsActive(
                        staffId,
                        "STAFF",
                        true
                ).orElseThrow(() -> new EntityNotFoundException(
                        "STAFF with ID " + staffId + " not found!"
                ));

        existingStaff.setFirstname(staffDto.getFirstname());
        existingStaff.setLastname(staffDto.getLastname());
        existingStaff.setEmail(staffDto.getEmail());
        existingStaff.setPassword(staffDto.getPassword());

        userRepository.save(existingStaff);
    }

    /**
     * Removes a staff member by ID.
     *
     * @param staffId the ID of the staff member to remove
     * @throws EntityNotFoundException if the staff member is not found
     */
    @Transactional
    public void removeStaff(final long staffId) {
        User user = userRepository
                .findUserByIdAndRoleNameAndIsActive(
                        staffId,
                        "STAFF",
                        true
                ).orElseThrow(() -> new EntityNotFoundException(
                        "STAFF with ID " + staffId + " not found!"
                ));

        user.setActive(false);
        userRepository.save(user);
    }

    /**
     * Retrieves a list of all staff members.
     *
     * @return a list of all staff members
     */
    public List<StaffDto> getAllStaff() {
        return userRepository.findAllByRoleNameAndIsActive("STAFF", true)
                .stream()
                .map(staffMapper::toDto)
                .toList();
    }

    /**
     * Retrieves a staff member by ID.
     *
     * @param staffId the ID of the staff member to retrieve
     * @return the staff member
     * @throws EntityNotFoundException if the staff member is not found
     */
    public StaffDto getStaffById(final long staffId) {
        User user = userRepository
                .findUserByIdAndRoleNameAndIsActive(
                        staffId, "STAFF", true
                )
                .orElseThrow(() -> new EntityNotFoundException(
                        "STAFF with ID " + staffId + " not found!"
                ));
        return staffMapper.toDto(user);
    }

    /**
     * Updates a specific field of a staff member.
     *
     * @param staffId      the ID of the staff member
     * @param fieldUpdater the field updater consumer
     */
    @Transactional
    public void updateStaffField(
            final long staffId, final Consumer<User> fieldUpdater
    ) {
        User existingStaff = userRepository
                .findUserByIdAndRoleNameAndIsActive(
                        staffId,
                        "STAFF",
                        true
                ).orElseThrow(() -> new EntityNotFoundException(
                        "STAFF with ID " + staffId + " not found!"
                ));

        fieldUpdater.accept(existingStaff);
        userRepository.save(existingStaff);
    }
}
