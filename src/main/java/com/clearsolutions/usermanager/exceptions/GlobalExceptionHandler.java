package com.clearsolutions.usermanager.exceptions;

import com.clearsolutions.usermanager.exceptions.custom.BasicApplicationException;
import com.clearsolutions.usermanager.exceptions.errors.ErrorResponse;
import com.clearsolutions.usermanager.utils.Logger;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles {@link BasicApplicationException} and logs the error
     * before returning an error response.
     *
     * @param ex {@link BasicApplicationException} exception to handle.
     * @return A ResponseEntity containing an error response
     * with the exception message and timestamp.
     */
    @ExceptionHandler(BasicApplicationException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(final BasicApplicationException ex) {
        Logger.error(ex.getClass().getSimpleName(), ex.getMessage());
        ErrorResponse response = new ErrorResponse(ex.getMessage());
        return new ResponseEntity<>(response, ex.getHttpStatus());
    }

    /**
     * Handles server exceptions and logs the error
     * before returning an internal server error response.
     *
     * @param ex The server exception to handle.
     * @return A ResponseEntity containing an error response
     * with the exception message and timestamp.
     */
    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            ConstraintViolationException.class,
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleBadRequest(final Exception ex) {
        var errorMessage = getValidationErrorMessage(ex);

        Logger.error(ex.getClass().getSimpleName(), errorMessage);
        ErrorResponse response = new ErrorResponse(errorMessage);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles server exceptions and logs the error
     * before returning an internal server error response.
     *
     * @param ex The server exception to handle.
     * @return A ResponseEntity containing an error response
     * with the exception message and timestamp.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleServerException(final Exception ex) {
        Logger.error(ex.getClass().getSimpleName(), ex.getMessage());
        ErrorResponse response = new ErrorResponse(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Extracts error messages from the given exception.
     *
     * @param ex The exception to extract error messages from.
     * @return A string containing the extracted error messages joined by commas.
     */
    private String getValidationErrorMessage(Exception ex) {
        var errorMessages = new ArrayList<String>();

        if (ex instanceof MethodArgumentNotValidException validationEx) {
            var bindingResult = validationEx.getBindingResult();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMessages.add(error.getDefaultMessage());
            }
        } else {
            errorMessages.add(ex.getMessage());
        }

        return String.join(", ", errorMessages);
    }
}
