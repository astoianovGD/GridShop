package com.bobocode.clients.user;

import com.bobocode.dto.users.UserDto;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.exceptions.ServiceUnavailableException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * Fallback factory for UserClient when user-service is unavailable or circuit breaker is open.
 */
@Slf4j
@Component
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {

    @Override
    public UserClient create(final Throwable cause) {
        return id -> {
            log.error("Fallback triggered for getUserById with ID {}. Cause: {}", id, cause.getMessage());
            if (cause instanceof FeignException.NotFound) {
                throw new EntityNotFoundException("User with ID " + id + " not found!");
            }
            throw new ServiceUnavailableException("User service is currently unavailable. Please try again later.");
        };
    }
}
