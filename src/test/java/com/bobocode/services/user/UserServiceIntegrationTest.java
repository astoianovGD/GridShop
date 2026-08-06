package com.bobocode.services.user;

import com.bobocode.entities.users.User;
import com.bobocode.enums.Gender;
import com.bobocode.exceptions.EmailAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional //making rollback automaticly
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void testIsEmailTakenForNonExistentEmail() {
        boolean taken = userService.isEmailTaken("nonexistent_unique_email@test.com");
        assertFalse(taken, "Email should not be taken");
    }

    @Test
    void testRegisterAndCheckEmail() {
        String uniqueEmail = "test.user." + System.currentTimeMillis() + "@test.com";

        User newUser = new User();
        newUser.setEmail(uniqueEmail);
        newUser.setPassword("securePassword123");
        newUser.setFirstName("John");
        newUser.setLastName("Doe");
        newUser.setAge(25);
        newUser.setGender(Gender.MALE);

        // check registration of new user
        assertDoesNotThrow(() -> userService.registerNewUser(newUser));

        // email is taken
        assertTrue(userService.isEmailTaken(uniqueEmail), "Email should now be marked as taken");

        // try register with existing email
        assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.validateEmailIsFree(uniqueEmail);
        });
    }
}