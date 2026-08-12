package com.bobocode.services.user;

import com.bobocode.dto.users.UserDto;
import com.bobocode.enums.Gender;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class UserConsoleViewServiceTest {

    private final UserConsoleViewService userConsoleViewService = new UserConsoleViewService();

    @Test
    void shouldPrintUserProfileSuccessfully() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setFirstname("Alex");
        userDto.setLastname("Stoianov");
        userDto.setAge(20);
        userDto.setGender(Gender.MALE);

        assertDoesNotThrow(() -> userConsoleViewService.printUserProfile(userDto));
    }
}