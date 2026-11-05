package com.bobocode.concurrency.ratelimiter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

/**
 * Servlet filter that applies per-client rate limiting using {@link RateLimiter}.
 *
 * <p>Identifies clients via the {@code X-Client-Id} HTTP header (or remote IP address when
 * {@code app.rate-limiter.enforce-all-requests=true}). Returns {@code 429 Too Many Requests}
 * when a client exceeds the configured request rate.
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    public static final String CLIENT_ID_HEADER = "X-Client-Id";

    private final RateLimiter rateLimiter;
    private final boolean enforceAllRequests;

    public RateLimitingFilter(
            @Value("${app.rate-limiter.max-requests:100}") final int maxRequests,
            @Value("${app.rate-limiter.window-seconds:1}") final long windowSeconds,
            @Value("${app.rate-limiter.enforce-all-requests:false}") final boolean enforceAllRequests
    ) {
        this.rateLimiter = new RateLimiter(maxRequests, Duration.ofSeconds(windowSeconds));
        this.enforceAllRequests = enforceAllRequests;
    }

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) throws ServletException, IOException {
        String clientKey = request.getHeader(CLIENT_ID_HEADER);
        if (clientKey == null || clientKey.isBlank()) {
            if (!enforceAllRequests) {
                filterChain.doFilter(request, response);
                return;
            }
            clientKey = request.getRemoteAddr();
        }

        if (!rateLimiter.tryAcquire(clientKey)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Too Many Requests for client: " + clientKey + "\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
