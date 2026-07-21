package com.bobocode.Exceptions;

/**
 * Exception thrown when a user attempts
 * to register with an email that is already taken.
 */
public class EmailAlreadyExistsException extends RuntimeException {

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message explaining the error
     */
    public EmailAlreadyExistsException(final String message) {
        super(message);
    }
}
