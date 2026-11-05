package com.bobocode.controllers.auth;

import com.bobocode.dto.auth.JwtResponse;
import com.bobocode.dto.auth.LoginRequest;
import com.bobocode.services.system.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling authentication endpoints.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Authenticates a user and returns a JWT token.
     * POST /api/v1/auth/login
     *
     * @param loginRequest login credentials
     * @return response entity containing the JWT response
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody final LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }
}
