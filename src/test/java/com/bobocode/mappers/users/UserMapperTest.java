package com.bobocode.mappers.users;

import com.bobocode.dto.users.UserDto;
import com.bobocode.entities.users.User;
import com.bobocode.enums.Gender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void shouldMapUserToDto() {
        // Arrange
        User user = new User();
        user.setId(7L);
        user.setEmail("alex.user@test.com");
        user.setPassword("secret_pass");
        user.setFirstname("Alex");
        user.setLastname("Stoianov");
        user.setAge(22);
        user.setGender(Gender.MALE);
        user.setActive(true);

        // Act
        UserDto dto = userMapper.toDto(user);

        // Assert
        assertNotNull(dto);
        assertEquals("alex.user@test.com", dto.getEmail());
        assertEquals("Alex", dto.getFirstname());
        assertEquals("Stoianov", dto.getLastname());
        assertEquals(22, dto.getAge());
        assertEquals(Gender.MALE, dto.getGender());
        // password should be ignored/omitted in Dto
    }

    @Test
    void shouldMapUserDtoToEntity() {
        // Arrange
        UserDto dto = new UserDto();
        dto.setEmail("dto.user@test.com");
        dto.setFirstname("David");
        dto.setLastname("Beckham");
        dto.setAge(30);
        dto.setGender(Gender.MALE);

        // Act
        User user = userMapper.toEntity(dto);

        // Assert
        assertNotNull(user);
        assertEquals(0L, user.getId()); // ignored
        assertTrue(user.isActive()); // constant = "true"
        assertNull(user.getRole()); // ignored
        assertEquals("dto.user@test.com", user.getEmail());
        assertEquals("David", user.getFirstname());
        assertEquals("Beckham", user.getLastname());
        assertEquals(30, user.getAge());
        assertEquals(Gender.MALE, user.getGender());
    }
}