package com.bobocode.Services.User;

import com.bobocode.Entities.Users.AbstractUser;
import com.bobocode.Entities.Users.Staff;
import com.bobocode.Exceptions.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StaffServiceTest {

    @Test
    @DisplayName("Should throw NullPointerException immediately upon creation if map is null")
    void testNullMap() {
        // @NonNull
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> new StaffService(null),
                "Creating StaffService with null map should throw NullPointerException");

        assertTrue(exception.getMessage().contains("is marked non-null but is null"));
    }

    @Test
    @DisplayName("Should successfully add new staff and assign an ID")
    void testAddNewStaff() {
        // Arrange
        Map<Long, AbstractUser> allUsers = new HashMap<>();
        StaffService staffService = new StaffService(allUsers);
        Staff newStaff = new Staff();
        newStaff.setFirstName("Alice");

        // Act
        staffService.addNewStaff(newStaff);

        // Assert
        assertNotNull(newStaff.getId(), "New staff should be assigned an ID");
        assertTrue(allUsers.containsKey(newStaff.getId()), "Map should contain the newly added staff");
        assertEquals(newStaff, allUsers.get(newStaff.getId()), "The object in the map should be the same as the added staff");
    }

    @Test
    @DisplayName("Should successfully edit an existing staff member")
    void testEditStaff_Success() {
        // Arrange
        Map<Long, AbstractUser> allUsers = new HashMap<>();
        Staff originalStaff = new Staff();
        originalStaff.setId(1L);
        originalStaff.setFirstName("Old Name");
        allUsers.put(1L, originalStaff);

        StaffService staffService = new StaffService(allUsers);

        Staff editedStaff = new Staff();
        editedStaff.setId(1L);
        editedStaff.setFirstName("New Name");

        // Act
        staffService.editStaff(1L, editedStaff);

        // Assert
        assertEquals("New Name", allUsers.get(1L).getFirstName(), "The staff name in the map should be updated");
        assertEquals(editedStaff, allUsers.get(1L), "The map should contain the updated staff object");
    }

    @Test
    @DisplayName("Should do nothing when trying to edit a staff member that does not exist")
    void testEditStaff_NotFound() {
        // Arrange
        Map<Long, AbstractUser> allUsers = new HashMap<>();
        Staff existingStaff = new Staff();
        existingStaff.setId(1L);
        allUsers.put(1L, existingStaff);

        StaffService staffService = new StaffService(allUsers);

        Staff editedStaff = new Staff();
        editedStaff.setId(99L); // ID, which we dont have in map

        // Act
        staffService.editStaff(99L, editedStaff);

        // Assert
        assertFalse(allUsers.containsKey(99L), "Map should not contain the non-existent ID after edit attempt");
        assertEquals(1, allUsers.size(), "Map size should remain unchanged");
    }

    @Test
    @DisplayName("Should successfully remove an existing staff member")
    void testRemoveStaff_Success() {
        // Arrange
        Map<Long, AbstractUser> allUsers = new HashMap<>();
        Staff existingStaff = new Staff();
        existingStaff.setId(1L);
        allUsers.put(1L, existingStaff);

        StaffService staffService = new StaffService(allUsers);

        // Act
        staffService.removeStaff(1L);

        // Assert
        assertFalse(allUsers.containsKey(1L), "Map should not contain the staff member after removal");
        assertTrue(allUsers.isEmpty(), "Map should be empty");
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when trying to remove a non-existent user")
    void testRemoveStaff_NotFound() {
        // Arrange
        Map<Long, AbstractUser> allUsers = new HashMap<>();
        StaffService staffService = new StaffService(allUsers);

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> staffService.removeStaff(99L),
                "Should throw EntityNotFoundException if staff ID is not found");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when trying to remove a user that is not a Staff member")
    void testRemoveStaff_WrongUserType() {
        // Arrange
        Map<Long, AbstractUser> allUsers = new HashMap<>();
        // new user which is not staff
        AbstractUser notAStaffMember = new AbstractUser() {};
        notAStaffMember.setId(2L);
        allUsers.put(2L, notAStaffMember);

        StaffService staffService = new StaffService(allUsers);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> staffService.removeStaff(2L),
                "Should throw IllegalArgumentException if the user is not a Staff member");
    }

    @Test
    @DisplayName("Should return a list of only Staff members")
    void testGetAllStaff() {
        // Arrange
        Map<Long, AbstractUser> allUsers = new HashMap<>();

        Staff staff1 = new Staff();
        staff1.setId(1L);

        Staff staff2 = new Staff();
        staff2.setId(2L);

        AbstractUser notAStaffMember = new AbstractUser() {};
        notAStaffMember.setId(3L);

        allUsers.put(1L, staff1);
        allUsers.put(2L, staff2);
        allUsers.put(3L, notAStaffMember);

        StaffService staffService = new StaffService(allUsers);

        // Act
        List<Staff> staffList = staffService.getAllStaff();

        // Assert
        assertEquals(2, staffList.size(), "List should contain exactly 2 staff members");
        assertTrue(staffList.contains(staff1));
        assertTrue(staffList.contains(staff2));
        assertFalse(staffList.contains((Staff) null));
    }

    @Test
    @DisplayName("Should successfully return a Staff member by ID")
    void testGetStaffById_Success() {
        // Arrange
        Map<Long, AbstractUser> allUsers = new HashMap<>();
        Staff existingStaff = new Staff();
        existingStaff.setId(1L);
        allUsers.put(1L, existingStaff);

        StaffService staffService = new StaffService(allUsers);

        // Act
        Staff retrievedStaff = staffService.getStaffById(1L);

        // Assert
        assertNotNull(retrievedStaff);
        assertEquals(existingStaff, retrievedStaff);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when getting Staff by non-existent ID")
    void testGetStaffById_NotFound() {
        // Arrange
        Map<Long, AbstractUser> allUsers = new HashMap<>();
        StaffService staffService = new StaffService(allUsers);

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> staffService.getStaffById(99L),
                "Should throw exception for missing ID");
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when getting ID that belongs to a non-Staff user")
    void testGetStaffById_WrongUserType() {
        // Arrange
        Map<Long, AbstractUser> allUsers = new HashMap<>();
        AbstractUser notAStaffMember = new AbstractUser() {};
        notAStaffMember.setId(2L);
        allUsers.put(2L, notAStaffMember);

        StaffService staffService = new StaffService(allUsers);

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> staffService.getStaffById(2L),
                "Should throw exception because the user is not a Staff member");
    }
}