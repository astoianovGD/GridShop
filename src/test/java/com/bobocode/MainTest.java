package com.bobocode;

import com.bobocode.Entities.Users.Admin;
import com.bobocode.Entities.Users.Staff;
import com.bobocode.Entities.Users.User;
import com.bobocode.Menus.AdminMenu;
import com.bobocode.Menus.AuthMenu;
import com.bobocode.Menus.StaffMenu;
import com.bobocode.Menus.UserMenu;
import com.bobocode.Utility.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Scanner;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MainTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    void testPrivateConstructor() throws Exception {
        Constructor<Main> constructor = Main.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()), "Constructor should be private");
        constructor.setAccessible(true);
        Main instance = constructor.newInstance();
        assertNotNull(instance);
    }

    @Test
    void main_WhenAdminExists_ShouldSkipAdminCreationAndShutdownWhenUserIsNull() {
        try (var mockedJdbc = mockConstruction(JdbcTemplate.class, (mock, context) -> {
            when(mock.findOne(anyString(), any(Function.class), eq(1))).thenReturn(true);
        });
             var mockedAuthMenu = mockConstruction(AuthMenu.class, (mock, context) -> {
                 when(mock.menu(any(Scanner.class))).thenReturn(null);
             })) {

            provideInput("\n");

            Main.main(new String[]{});

            String output = outContent.toString();
            assertTrue(output.contains("Admin already exists in Database. Skipping setup."));
            assertTrue(output.contains("Shutting down the system..."));
        }
    }

    @Test
    void main_WhenAdminDoesNotExist_ShouldCreateAdminAndThenShutdown() {
        try (var mockedJdbc = mockConstruction(JdbcTemplate.class, (mock, context) -> {
            when(mock.findOne(anyString(), any(Function.class), eq(1))).thenReturn(false);
        });
             var mockedAuthMenu = mockConstruction(AuthMenu.class, (mock, context) -> {
                 when(mock.menu(any(Scanner.class))).thenReturn(null);
             })) {

            String input = "John\nDoe\nadmin@gmail.com\nPassword123!\n";
            provideInput(input);

            Main.main(new String[]{});

            String output = outContent.toString();
            assertTrue(output.contains("SYSTEM SETUP: CREATE FIRST ADMIN"));
            assertTrue(output.contains("Admin successfully created in Database!"));
            assertTrue(output.contains("Shutting down the system..."));

            JdbcTemplate createdJdbc = mockedJdbc.constructed().get(0);
            verify(createdJdbc, times(1)).execute(
                    anyString(),
                    eq("admin@gmail.com"),
                    eq("Password123!"),
                    eq("Doe"),
                    eq("John"),
                    eq(1)
            );
        }
    }

    @Test
    void main_WhenUserLogsInAsAdminStaffAndUser_ShouldExecuteRespectiveMenusAndShutdown() {
        Admin admin = new Admin();
        Staff staff = new Staff();
        User user = new User();

        try (var mockedJdbc = mockConstruction(JdbcTemplate.class, (mock, context) -> {
            when(mock.findOne(anyString(), any(Function.class), eq(1))).thenReturn(true);
        });
             var mockedAuthMenu = mockConstruction(AuthMenu.class, (mock, context) -> {
                 when(mock.menu(any(Scanner.class)))
                         .thenReturn(admin)
                         .thenReturn(staff)
                         .thenReturn(user)
                         .thenReturn(null);
             });
             var mockedAdminMenu = mockConstruction(AdminMenu.class);
             var mockedStaffMenu = mockConstruction(StaffMenu.class);
             var mockedUserMenu = mockConstruction(UserMenu.class)) {

            provideInput("\n");

            Main.main(new String[]{});

            AdminMenu adminMenu = mockedAdminMenu.constructed().get(0);
            StaffMenu staffMenu = mockedStaffMenu.constructed().get(0);
            UserMenu userMenu = mockedUserMenu.constructed().get(0);

            verify(adminMenu, times(1)).menu(any(Scanner.class));
            verify(staffMenu, times(1)).menu(any(Scanner.class));
            verify(userMenu, times(1)).menu(eq(user), any(Scanner.class));

            assertTrue(outContent.toString().contains("Shutting down the system..."));
        }
    }

    private void provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }
}