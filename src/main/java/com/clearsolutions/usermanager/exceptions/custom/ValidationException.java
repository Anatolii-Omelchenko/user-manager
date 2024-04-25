package com.clearsolutions.usermanager.exceptions.custom;

import org.springframework.http.HttpStatus;

public class ValidationException extends BasicApplicationException {

    /**
     * Constructs MethodArgumentNotValidException with the specified error message.
     *
     * @param message Error message explaining the reason for the exception.
     */
    public ValidationException(final String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
