package com.bobocode.Services.User;

import com.bobocode.Entities.Users.AbstractUser;
import com.bobocode.Entities.Users.Admin;
import com.bobocode.Entities.Users.User;
import com.bobocode.Enums.Gender;
import com.bobocode.Exceptions.EntityNotFoundException;
import com.bobocode.Utility.JdbcTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private AuthService authService;

    @Test
    void signIn_WhenAdminCredentialsValid_ShouldReturnAdminAndActivate() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString("role_name")).thenReturn("ADMIN");
        when(rs.getLong("user_id")).thenReturn(1L);
        when(rs.getString("email")).thenReturn("admin@test.com");
        when(rs.getString("password")).thenReturn("pass");
        when(rs.getString("lastname")).thenReturn("Doe");
        when(rs.getString("firstname")).thenReturn("John");

        when(jdbcTemplate.findOne(anyString(), any(Function.class), eq("admin@test.com"), eq("pass")))
                .thenAnswer(invocation -> {
                    Function<ResultSet, AbstractUser> mapper = invocation.getArgument(1);
                    return mapper.apply(rs);
                });

        AbstractUser result = authService.signIn("admin@test.com", "pass");

        assertNotNull(result);
        Admin admin =assertInstanceOf(Admin.class, result);
        assertEquals(1L, admin.getId());

        verify(jdbcTemplate, times(1)).execute(anyString(), (Object[]) any());
    }

    @Test
    void signIn_WhenUserCredentialsValid_ShouldReturnUserWithDetails() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString("role_name")).thenReturn("USER");
        when(rs.getLong("user_id")).thenReturn(2L);
        when(rs.getString("email")).thenReturn("user@test.com");
        when(rs.getString("password")).thenReturn("pass");
        when(rs.getString("lastname")).thenReturn("Smith");
        when(rs.getString("firstname")).thenReturn("Jane");
        when(rs.getInt("age")).thenReturn(25);
        when(rs.getString("gender")).thenReturn("FEMALE");

        when(jdbcTemplate.findOne(anyString(), any(Function.class), eq("user@test.com"), eq("pass")))
                .thenAnswer(invocation -> {
                    Function<ResultSet, AbstractUser> mapper = invocation.getArgument(1);
                    return mapper.apply(rs);
                });

        AbstractUser result = authService.signIn("user@test.com", "pass");

        assertNotNull(result);
        User regularUser = assertInstanceOf(User.class, result);
        assertEquals(25, regularUser.getAge());
        assertEquals(Gender.FEMALE, regularUser.getGender());
    }

    @Test
    void signIn_WhenInvalidCredentials_ShouldThrowEntityNotFoundException() {
        when(jdbcTemplate.findOne(anyString(), any(Function.class), eq("wrong@test.com"), eq("wrong")))
                .thenReturn(null);

        assertThrows(EntityNotFoundException.class, () ->
                authService.signIn("wrong@test.com", "wrong")
        );
        verify(jdbcTemplate, never()).execute(anyString(), (Object[]) any());
    }
}