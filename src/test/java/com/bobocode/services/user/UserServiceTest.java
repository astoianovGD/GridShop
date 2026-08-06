package com.bobocode.services.user;

import com.bobocode.entities.users.User;
import com.bobocode.enums.Gender;
import com.bobocode.exceptions.EmailAlreadyExistsException;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.utility.CustomJdbcTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private CustomJdbcTemplate customJdbcTemplate;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@gmail.com");
        testUser.setPassword("password123");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setAge(25);
        testUser.setGender(Gender.MALE);
    }

    @Test
    void registerNewUser_shouldInsertUserAndCreateBucket() {
        when(customJdbcTemplate.findOne(eq("SELECT user_id FROM users WHERE email = ?"), any(), eq(testUser.getEmail())))
                .thenReturn(1L);

        userService.registerNewUser(testUser);

        verify(customJdbcTemplate, times(1)).execute(
                eq("INSERT INTO users (email, password, lastname, firstname, age, gender, role_id) VALUES (?, ?, ?, ?, ?, ?, (SELECT role_id FROM roles WHERE name = 'USER'))"),
                eq(testUser.getEmail()),
                eq(testUser.getPassword()),
                eq(testUser.getLastName()),
                eq(testUser.getFirstName()),
                eq(testUser.getAge()),
                eq("MALE")
        );

        verify(customJdbcTemplate, times(1)).findOne(
                eq("SELECT user_id FROM users WHERE email = ?"),
                any(),
                eq(testUser.getEmail())
        );

        verify(customJdbcTemplate, times(1)).execute(eq("INSERT INTO bucket (user_id) VALUES (?)"), eq(1L));
    }

    @Test
    void deleteUserAccount_shouldDeactivateUser_whenUserExists() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(customJdbcTemplate.findOne(any(String.class), any(), eq(1L))).thenAnswer(invocation -> {
            Function<ResultSet, User> mapper = invocation.getArgument(1);
            return mapper.apply(resultSet);
        });

        userService.deleteUserAccount(1L);

        verify(customJdbcTemplate, times(1)).execute(eq("UPDATE users SET is_active = false WHERE user_id = ?"), eq(1L));
    }

    @Test
    void deleteUserAccount_shouldThrowException_whenUserNotFound() {
        when(customJdbcTemplate.findOne(any(String.class), any(), eq(1L))).thenReturn(null);

        assertThatThrownBy(() -> userService.deleteUserAccount(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User with ID 1 not found!");

        verify(customJdbcTemplate, never()).execute(any(String.class), any(Long.class));
    }

    @Test
    void editPersonalInformation_shouldUpdateUser_whenUserExists() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(customJdbcTemplate.findOne(any(String.class), any(), eq(1L))).thenAnswer(invocation -> {
            Function<ResultSet, User> mapper = invocation.getArgument(1);
            return mapper.apply(resultSet);
        });

        User updatedInfo = new User();
        updatedInfo.setEmail("new@gmail.com");
        updatedInfo.setPassword("newpass");
        updatedInfo.setLastName("Smith");
        updatedInfo.setFirstName("Jane");
        updatedInfo.setAge(30);
        updatedInfo.setGender(Gender.FEMALE);

        userService.editPersonalInformation(1L, updatedInfo);

        verify(customJdbcTemplate, times(1)).execute(
                eq("UPDATE users SET email = ?, password = ?, lastname = ?, firstname = ?, age = ?, gender = ? WHERE user_id = ?"),
                eq("new@gmail.com"), eq("newpass"), eq("Smith"), eq("Jane"), eq(30), eq("FEMALE"), eq(1L)
        );
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(customJdbcTemplate.findMany(any(String.class), any())).thenAnswer(invocation -> {
            Function<ResultSet, User> mapper = invocation.getArgument(1);
            return List.of(mapper.apply(resultSet));
        });

        List<User> users = userService.getAllUsers();

        assertThat(users).hasSize(1);
    }

    @Test
    void getUserById_shouldReturnUser_whenFound() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(customJdbcTemplate.findOne(any(String.class), any(), eq(1L))).thenAnswer(invocation -> {
            Function<ResultSet, User> mapper = invocation.getArgument(1);
            return mapper.apply(resultSet);
        });

        User user = userService.getUserById(1L);

        assertThat(user).isNotNull();
    }

    @Test
    void getUserById_shouldThrowException_whenNotFound() {
        when(customJdbcTemplate.findOne(any(String.class), any(), eq(1L))).thenReturn(null);

        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User with ID 1 not found!");
    }

    @Test
    void isEmailTaken_shouldReturnTrue_whenEmailExists() {
        when(customJdbcTemplate.findOne(any(String.class), any(), eq("taken@gmail.com"))).thenReturn(true);

        boolean taken = userService.isEmailTaken("taken@gmail.com");

        assertThat(taken).isTrue();
    }

    @Test
    void validateEmailIsFree_shouldThrowException_whenEmailTaken() {
        when(customJdbcTemplate.findOne(any(String.class), any(), eq("taken@gmail.com"))).thenReturn(true);

        assertThatThrownBy(() -> userService.validateEmailIsFree("taken@gmail.com"))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("Error 409: This email is already registered!");
    }
}