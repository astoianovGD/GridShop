package com.bobocode.clients.user;

import com.bobocode.dto.users.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for communicating with user-service.
 */
@FeignClient(name = "user-service")
public interface UserClient {

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id user ID
     * @return the user DTO
     */
    @GetMapping("/api/v1/users/{id}")
    UserDto getUserById(@PathVariable("id") Long id);
}

