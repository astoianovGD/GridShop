package com.bobocode.clients.user;

import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.exceptions.ServiceUnavailableException;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserClientFallbackFactoryTest {

    private UserClientFallbackFactory fallbackFactory;

    @BeforeEach
    void setUp() {
        fallbackFactory = new UserClientFallbackFactory();
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException on 404")
    void shouldThrowEntityNotFoundExceptionOn404() {
        Request request = Request.create(Request.HttpMethod.GET, "/api/v1/users/1",
                Collections.emptyMap(), null, new RequestTemplate());
        FeignException.NotFound notFound = new FeignException.NotFound("Not Found", request, null, null);

        UserClient client = fallbackFactory.create(notFound);

        assertThrows(EntityNotFoundException.class, () -> client.getUserById(1L));
    }

    @Test
    @DisplayName("Should throw ServiceUnavailableException on generic failure")
    void shouldThrowServiceUnavailableExceptionOnGenericFailure() {
        RuntimeException failure = new RuntimeException("Connection refused");

        UserClient client = fallbackFactory.create(failure);

        ServiceUnavailableException ex = assertThrows(ServiceUnavailableException.class, () -> client.getUserById(1L));
        assertTrue(ex.getMessage().contains("currently unavailable"));
    }
}
