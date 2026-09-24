package com.bobocode.services.system;

import com.bobocode.dto.auth.JwtResponse;
import com.bobocode.dto.auth.LoginRequest;
import com.bobocode.entities.users.User;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.repositories.users.UserRepository;
import com.bobocode.security.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for handling user authentication and token generation.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    /**
     * Authenticates user credentials and generates a JWT token.
     *
     * @param loginRequest login credentials
     * @return response with JWT token and user info
     */
    @Transactional(readOnly = true)
    public JwtResponse login(final LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found with email: " + loginRequest.getEmail()
                ));

        String role = user.getRole() != null ? user.getRole().getName() : "USER";
        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), role);

        log.info("User {} successfully authenticated with role {}", user.getEmail(), role);

        return JwtResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .role(role)
                .build();
    }
}
