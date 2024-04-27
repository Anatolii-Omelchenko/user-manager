package com.clearsolutions.usermanager.exceptions;

import com.clearsolutions.usermanager.exceptions.custom.BasicApplicationException;
import com.clearsolutions.usermanager.exceptions.custom.EntityAlreadyExistsException;
import com.clearsolutions.usermanager.exceptions.custom.EntityNotFoundException;
import com.clearsolutions.usermanager.exceptions.errors.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testing GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    private final String entityType = "User";
    private final String entityDetails = "Id: 999";

    @Test
    @DisplayName("Handle EntityNotFoundException: Should Return Correct Response")
    void testEntityNotFoundExceptionHandling() {
        // Prepare
        BasicApplicationException exception = new EntityNotFoundException(entityType, entityDetails);

        // Execute
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleCustomException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().timestamp()).isGreaterThan(0L);
        assertThat(response.getBody().message()).isEqualTo(exception.getMessage());
    }

    @Test
    @DisplayName("Handle EntityAlreadyExistsException: Should Return Correct Response")
    void testEntityAlreadyExistsExceptionHandling() {
        // Prepare
        BasicApplicationException exception = new EntityAlreadyExistsException(entityType, entityDetails);

        // Execute
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleCustomException(exception);

        //Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().timestamp()).isGreaterThan(0L);
        assertThat(response.getBody().message()).isEqualTo(exception.getMessage());
    }

    @Test
    @DisplayName("Handle Server Exception: Should Return Correct Response")
    void testServerExceptionHandling() {
        // Prepare
        Exception exception = new NumberFormatException("Unable to parse string as a number: abc123");

        // Execute
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleServerException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().timestamp()).isGreaterThan(0L);
        assertThat(response.getBody().message()).isEqualTo(exception.getMessage());
    }
}
