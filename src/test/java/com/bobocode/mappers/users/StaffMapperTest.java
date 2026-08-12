package com.bobocode.mappers.users;

import com.bobocode.dto.users.StaffDto;
import com.bobocode.entities.users.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class StaffMapperTest {

    @Autowired
    private StaffMapper staffMapper;

    @Test
    void shouldMapUserToStaffDto() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setFirstname("Jane");
        user.setLastname("Doe");
        user.setEmail("jane.doe@test.com");
        user.setPassword("secret");

        // Act
        StaffDto dto = staffMapper.toDto(user);

        // Assert
        assertNotNull(dto);
        assertEquals("Jane", dto.getFirstname());
        assertEquals("Doe", dto.getLastname());
        assertEquals("jane.doe@test.com", dto.getEmail());
    }

    @Test
    void shouldMapStaffDtoToEntity() {
        // Arrange
        StaffDto dto = new StaffDto();
        dto.setFirstname("Bob");
        dto.setLastname("Builder");
        dto.setEmail("bob@test.com");

        // Act
        User user = staffMapper.toEntity(dto);

        // Assert
        assertNotNull(user);
        assertEquals(0L, user.getId()); // ignored
        assertEquals(0, user.getAge()); // ignored
        assertNull(user.getGender()); // ignored
        assertTrue(user.isActive()); // constant = "true"
        assertNull(user.getRole()); // ignored
        assertEquals("Bob", user.getFirstname());
        assertEquals("Builder", user.getLastname());
        assertEquals("bob@test.com", user.getEmail());
    }
}