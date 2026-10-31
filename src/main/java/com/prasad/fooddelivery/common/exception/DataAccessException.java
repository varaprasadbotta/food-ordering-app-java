package com.prasad.fooddelivery.common.exception;

// Unchecked wrapper for database failures, so services don't have to
// deal with JDBC's checked SQLException (and stay database-agnostic).
public class DataAccessException extends RuntimeException {

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
