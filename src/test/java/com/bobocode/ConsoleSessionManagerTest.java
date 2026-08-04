package com.bobocode;

import com.bobocode.entities.users.Admin;
import com.bobocode.entities.users.Staff;
import com.bobocode.entities.users.User;
import com.bobocode.menus.AdminMenu;
import com.bobocode.menus.AuthMenu;
import com.bobocode.menus.StaffMenu;
import com.bobocode.menus.UserMenu;
import com.bobocode.services.system.SystemInitializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsoleSessionManagerTest {

    @Mock
    private AuthMenu authMenu;

    @Mock
    private AdminMenu adminMenu;

    @Mock
    private StaffMenu staffMenu;

    @Mock
    private UserMenu userMenu;

    @Mock
    private SystemInitializer systemInitializer;

    @InjectMocks
    private ConsoleSessionManager sessionManager;

    @Test
    @DisplayName("Should initialize system and exit when auth returns null")
    void shouldInitializeAndExitWhenAuthNull() {
        // Підготовка вводу для Scanner (щоб уникнути NoSuchElementException, імітуємо порожній ввід або Enter)
        provideInput("");

        when(authMenu.menu(any())).thenReturn(null);

        // Виконання
        sessionManager.startSession();

        // Перевірки
        verify(systemInitializer).initializeSystem(any());
        verify(authMenu).menu(any());
        verifyNoInteractions(adminMenu, staffMenu, userMenu);
    }

    @Test
    @DisplayName("Should route to AdminMenu when logged in user is Admin")
    void shouldRouteToAdminMenu() {
        provideInput("");

        Admin admin = new Admin();
        // Спочатку повертаємо адміна, потім null для виходу з циклу
        when(authMenu.menu(any())).thenReturn(admin).thenReturn(null);

        sessionManager.startSession();

        verify(systemInitializer).initializeSystem(any());
        verify(adminMenu).menu(any());
        verifyNoInteractions(staffMenu, userMenu);
    }

    @Test
    @DisplayName("Should route to StaffMenu when logged in user is Staff")
    void shouldRouteToStaffMenu() {
        provideInput("");

        Staff staff = new Staff();
        when(authMenu.menu(any())).thenReturn(staff).thenReturn(null);

        sessionManager.startSession();

        verify(systemInitializer).initializeSystem(any());
        verify(staffMenu).menu(any());
        verifyNoInteractions(adminMenu, userMenu);
    }

    @Test
    @DisplayName("Should route to UserMenu when logged in user is standard User")
    void shouldRouteToUserMenu() {
        provideInput("");

        User user = new User();
        when(authMenu.menu(any())).thenReturn(user).thenReturn(null);

        sessionManager.startSession();

        verify(systemInitializer).initializeSystem(any());
        verify(userMenu).menu(eq(user), any());
        verifyNoInteractions(adminMenu, staffMenu);
    }

    /**
     * Helper method to redirect System.in for the Scanner used in sessionManager.
     */
    private void provideInput(String data) {
        InputStream testInput = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
        System.setIn(testInput);
    }
}