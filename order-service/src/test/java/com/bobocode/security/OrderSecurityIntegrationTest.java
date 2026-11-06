package com.bobocode.security;

import com.bobocode.clients.user.UserClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.kafka.listener.auto-startup=false")
@AutoConfigureMockMvc
class OrderSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JWTUtil jwtUtil;

    @MockBean
    private UserClient userClient;

    @Test
    @DisplayName("Unauthenticated request to bucket should return 401")
    void unauthenticatedRequest_Returns401() throws Exception {
        mockMvc.perform(get("/api/v1/users/4/bucket"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("User accessing their own bucket should return 200")
    void userAccessingOwnBucket_Returns200() throws Exception {
        String token = jwtUtil.generateToken("misha@gmail.com", 4L, "USER");

        mockMvc.perform(get("/api/v1/users/4/bucket")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("User accessing another user's bucket should return 403")
    void userAccessingOtherBucket_Returns403() throws Exception {
        String token = jwtUtil.generateToken("misha@gmail.com", 4L, "USER");

        mockMvc.perform(get("/api/v1/users/5/bucket")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Admin accessing any user's bucket should return 200")
    void adminAccessingAnyBucket_Returns200() throws Exception {
        String token = jwtUtil.generateToken("admin@gmail.com", 1L, "ADMIN");

        mockMvc.perform(get("/api/v1/users/5/bucket")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("User accessing their own orders should return 200")
    void userAccessingOwnOrders_Returns200() throws Exception {
        String token = jwtUtil.generateToken("misha@gmail.com", 4L, "USER");

        mockMvc.perform(get("/api/v1/users/4/orders")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("User accessing another user's orders should return 403")
    void userAccessingOtherOrders_Returns403() throws Exception {
        String token = jwtUtil.generateToken("misha@gmail.com", 4L, "USER");

        mockMvc.perform(get("/api/v1/users/5/orders")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Admin accessing any user's orders should return 200")
    void adminAccessingAnyOrders_Returns200() throws Exception {
        String token = jwtUtil.generateToken("admin@gmail.com", 1L, "ADMIN");

        mockMvc.perform(get("/api/v1/users/5/orders")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
