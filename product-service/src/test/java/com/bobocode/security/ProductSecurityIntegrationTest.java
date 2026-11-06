package com.bobocode.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.kafka.listener.auto-startup=false",
        "spring.kafka.admin.auto-create=false"
})
@AutoConfigureMockMvc
class ProductSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JWTUtil jwtUtil;

    @Test
    @DisplayName("Public GET /api/v1/products should return 200 without token")
    void publicGetProducts_WithoutToken_Returns200() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Public GET /api/v1/products/{id} should return 200 without token")
    void publicGetProductById_WithoutToken_Returns200() throws Exception {
        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Public GET /api/v1/categories should return 200 without token")
    void publicGetCategories_WithoutToken_Returns200() throws Exception {
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/v1/products without token should return 401 Unauthorized")
    void createProduct_WithoutToken_Returns401() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"New Item\", \"price\": 20.0, \"categoryId\": 1}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/v1/products with USER role should return 403 Forbidden")
    void createProduct_WithUserRole_Returns403() throws Exception {
        String token = jwtUtil.generateToken("user@gmail.com", 4L, "USER");

        mockMvc.perform(post("/api/v1/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"New Item\", \"price\": 20.0, \"categoryId\": 1}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/v1/products with STAFF role should return 201 Created")
    void createProduct_WithStaffRole_Returns201() throws Exception {
        String token = jwtUtil.generateToken("staff@gmail.com", 2L, "STAFF");

        mockMvc.perform(post("/api/v1/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Staff Laptop\", \"price\": 1200.0, \"categoryId\": 1}"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("DELETE /api/v1/products/{id} with USER role should return 403 Forbidden")
    void deleteProduct_WithUserRole_Returns403() throws Exception {
        String token = jwtUtil.generateToken("user@gmail.com", 4L, "USER");

        mockMvc.perform(delete("/api/v1/products/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /api/v1/products/{id} with ADMIN role should return 204 No Content")
    void deleteProduct_WithAdminRole_Returns204() throws Exception {
        String token = jwtUtil.generateToken("admin@gmail.com", 1L, "ADMIN");

        mockMvc.perform(delete("/api/v1/products/2")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }
}
