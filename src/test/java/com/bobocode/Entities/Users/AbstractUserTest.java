package com.bobocode.Entities.Users;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbstractUserTest {

    @Test
    @DisplayName("generateNextId() should increment and return unique IDs")
    void testGenerateNextId() {
        // Arrange & Act
        long id1 = AbstractUser.generateNextId();
        long id2 = AbstractUser.generateNextId();
        long id3 = AbstractUser.generateNextId();

        // Assert
        assertTrue(id2 > id1, "Second ID should be greater than the first");
        assertTrue(id3 > id2, "Third ID should be greater than the second");
        assertEquals(id1 + 1, id2, "ID should increment exactly by 1");
    }

    @Test
    @DisplayName("toString() should format correctly using concrete subclass name")
    void testToStringFormatting() {
        // Arrange
        Admin admin = new Admin();
        admin.setId(10L);
        admin.setFirstName("John");
        admin.setLastName("Doe");
        admin.setEmail("admin@bobocode.com");

        Staff staff = new Staff();
        staff.setId(11L);
        staff.setFirstName("Jane");
        staff.setLastName("Smith");
        staff.setEmail("staff@bobocode.com");

        // Act
        String adminString = admin.toString();
        String staffString = staff.toString();

        // Assert
        String expectedAdminStr = "[Admin] ID: 10 | Name: John Doe | Email: admin@bobocode.com";
        String expectedStaffStr = "[Staff] ID: 11 | Name: Jane Smith | Email: staff@bobocode.com";

        assertEquals(expectedAdminStr, adminString, "Admin toString format is incorrect");
        assertEquals(expectedStaffStr, staffString, "Staff toString format is incorrect");
    }

    @Test
    @DisplayName("EqualsAndHashCode should work correctly for subclasses")
    void testEqualsAndHashCode() {
        // Arrange
        Admin admin1 = new Admin();
        admin1.setId(1L);
        admin1.setEmail("test@test.com");

        Admin admin2 = new Admin();
        admin2.setId(1L);
        admin2.setEmail("test@test.com");

        Admin admin3 = new Admin();
        admin3.setId(2L); // Different ID

        // Assert
        assertEquals(admin1, admin2, "Users with same data should be equal");
        assertEquals(admin1.hashCode(), admin2.hashCode(), "HashCodes should match");
        assertNotEquals(admin1, admin3, "Users with different data should not be equal");
    }
}