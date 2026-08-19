package com.bobocode.controllers.users;

import com.bobocode.dto.users.StaffDto;
import com.bobocode.dto.users.StaffRegistrationDto;
import com.bobocode.services.user.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    /**
     * Retrieves all staff members.
     * GET /api/v1/staff
     *
     * @return a list of staff DTOs
     */
    @GetMapping
    public List<StaffDto> getAllStaff() {
        return staffService.getAllStaff();
    }

    /**
     * Retrieves a specific staff member by their ID.
     * GET /api/v1/staff/{id}
     *
     * @param id the ID of the staff member
     * @return the matching staff DTO
     */
    @GetMapping("/{id}")
    public StaffDto getStaffById(@PathVariable long id) {
        return staffService.getStaffById(id);
    }

    /**
     * Updates an existing staff member by ID.
     * PUT /api/v1/staff/{id}
     *
     * @param id       the ID of the staff member to update
     * @param staffDto the payload containing updated details
     */
    @PutMapping("/{id}")
    public void updateStaff(@PathVariable long id, @RequestBody StaffDto staffDto) {
        staffService.editStaff(id, staffDto);
    }

    /**
     * Creates a new staff member.
     * POST /api/v1/staff
     *
     * @param staffRegistrationDto the payload containing registration details
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) //201
    public void createNewStaff(@RequestBody StaffRegistrationDto staffRegistrationDto) {
        staffService.addNewStaff(staffRegistrationDto);
    }

    /**
     * Deletes a staff member by ID.
     * DELETE /api/v1/staff/{id}
     *
     * @param id the ID of the staff member to delete
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) //204
    public void deleteStaffById(@PathVariable long id) {
        staffService.removeStaff(id);
    }
}