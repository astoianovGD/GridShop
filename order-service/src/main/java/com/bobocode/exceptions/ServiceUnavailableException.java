package com.bobocode.exceptions;

/**
 * Exception thrown when an external service is temporarily unavailable.
 */
public class ServiceUnavailableException extends RuntimeException {

    /**
     * Constructs a new ServiceUnavailableException with the specified message.
     *
     * @param message detail message
     */
    public ServiceUnavailableException(final String message) {
        super(message);
    }
}
