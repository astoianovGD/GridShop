package com.bobocode.mappers.users;

import com.bobocode.dto.users.UserRegistrationDto;
import com.bobocode.entities.users.User;
import com.bobocode.enums.Gender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserRegistrationMapperTest {

    @Autowired
    private UserRegistrationMapper userRegistrationMapper;

    @Test
    void shouldMapUserRegistrationDtoToEntity() {
        // Arrange
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setEmail("reg@test.com");
        dto.setPassword("password123");
        dto.setFirstname("John");
        dto.setLastname("Doe");
        dto.setAge(25);
        dto.setGender(Gender.MALE);

        // Act
        User user = userRegistrationMapper.toEntity(dto);

        // Assert
        assertNotNull(user);
        assertEquals(0L, user.getId()); // ignored
        assertNull(user.getRole()); // ignored
        assertTrue(user.isActive()); // constant = "true"
        assertEquals("reg@test.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("John", user.getFirstname());
        assertEquals("Doe", user.getLastname());
        assertEquals(25, user.getAge());
        assertEquals(Gender.MALE, user.getGender());
    }
}