package com.bobocode.Services.User;

import com.bobocode.Entities.Users.Staff;
import com.bobocode.Exceptions.EntityNotFoundException;
import com.bobocode.Utility.JdbcTemplate;
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
class StaffServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private StaffService staffService;

    private Staff testStaff;

    @BeforeEach
    void setUp() {
        testStaff = new Staff();
        testStaff.setId(10L);
        testStaff.setEmail("staff@bobocode.com");
        testStaff.setPassword("admin123");
        testStaff.setFirstName("Alice");
        testStaff.setLastName("Smith");
    }

    @Test
    void addNewStaff_shouldExecuteInsert() {
        staffService.addNewStaff(testStaff);

        verify(jdbcTemplate, times(1)).execute(
                any(String.class),
                eq(testStaff.getEmail()),
                eq(testStaff.getPassword()),
                eq(testStaff.getLastName()),
                eq(testStaff.getFirstName()),
                eq(null),
                eq(null)
        );
    }

    @Test
    void editStaff_shouldUpdateStaff_whenExists() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(jdbcTemplate.findOne(any(String.class), any(), eq(10L))).thenAnswer(invocation -> {
            Function<ResultSet, Staff> mapper = invocation.getArgument(1);
            return mapper.apply(resultSet);
        });

        Staff edited = new Staff();
        edited.setEmail("newstaff@bobocode.com");
        edited.setPassword("newpass");
        edited.setLastName("Johnson");
        edited.setFirstName("Bob");

        staffService.editStaff(10L, edited);

        verify(jdbcTemplate, times(1)).execute(
                any(String.class),
                eq("newstaff@bobocode.com"),
                eq("newpass"),
                eq("Johnson"),
                eq("Bob"),
                eq(10L)
        );
    }

    @Test
    void removeStaff_shouldDeactivate_whenExists() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(jdbcTemplate.findOne(any(String.class), any(), eq(10L))).thenAnswer(invocation -> {
            Function<ResultSet, Staff> mapper = invocation.getArgument(1);
            return mapper.apply(resultSet);
        });

        staffService.removeStaff(10L);

        verify(jdbcTemplate, times(1)).execute(eq("UPDATE users SET is_active = false WHERE user_id = ?"), eq(10L));
    }

    @Test
    void getAllStaff_shouldReturnList() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(jdbcTemplate.findMany(any(String.class), any())).thenAnswer(invocation -> {
            Function<ResultSet, Staff> mapper = invocation.getArgument(1);
            return List.of(mapper.apply(resultSet));
        });

        List<Staff> staffList = staffService.getAllStaff();

        assertThat(staffList).hasSize(1);
    }

    @Test
    void getStaffById_shouldThrowException_whenNotFound() {
        when(jdbcTemplate.findOne(any(String.class), any(), eq(99L))).thenReturn(null);

        assertThatThrownBy(() -> staffService.getStaffById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("HTTP STATUS 404 : Staff with ID 99 not found!");
    }
}