package com.bobocode.Exceptions;

/**
 * Exception thrown when
 * a requested entity cannot be found in the database or collection.
 */
public class EntityNotFoundException extends RuntimeException {

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message explaining the error
     */
    public EntityNotFoundException(final String message) {
        super(message);
    }
}
