package com.bobocode.mappers.users;

import com.bobocode.dto.users.StaffRegistrationDto;
import com.bobocode.entities.users.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class StaffRegistrationMapperTest {

    @Autowired
    private StaffRegistrationMapper staffRegistrationMapper;

    @Test
    void shouldMapStaffRegistrationDtoToEntity() {
        // Arrange
        StaffRegistrationDto dto = new StaffRegistrationDto();
        dto.setFirstname("Charlie");
        dto.setLastname("Brown");
        dto.setEmail("charlie@test.com");
        dto.setPassword("password123");

        // Act
        User user = staffRegistrationMapper.toEntity(dto);

        // Assert
        assertNotNull(user);
        assertEquals(0L, user.getId()); // ignored
        assertNull(user.getRole()); // ignored
        assertEquals(0, user.getAge()); // ignored
        assertNull(user.getGender()); // ignored
        assertTrue(user.isActive()); // constant = "true"
        assertEquals("Charlie", user.getFirstname());
        assertEquals("Brown", user.getLastname());
        assertEquals("charlie@test.com", user.getEmail());
        assertEquals("password123", user.getPassword());
    }
}