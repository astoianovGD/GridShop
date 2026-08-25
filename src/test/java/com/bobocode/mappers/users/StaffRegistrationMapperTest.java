package com.bobocode.mappers.users;

import com.bobocode.dto.users.StaffRegistrationDto;
import com.bobocode.entities.users.User;
import com.bobocode.enums.Gender;
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
        dto.setAge(30);
        dto.setGender(Gender.MALE);

        // Act
        User user = staffRegistrationMapper.toEntity(dto);

        // Assert
        assertNotNull(user);
        assertNull(user.getId()); // ignored, Long is null
        assertNull(user.getRole()); // ignored
        assertEquals(30, user.getAge());
        assertEquals(Gender.MALE, user.getGender());
        assertTrue(user.isActive()); // constant = "true"
        assertEquals("Charlie", user.getFirstname());
        assertEquals("Brown", user.getLastname());
        assertEquals("charlie@test.com", user.getEmail());
        assertEquals("password123", user.getPassword());
    }
}