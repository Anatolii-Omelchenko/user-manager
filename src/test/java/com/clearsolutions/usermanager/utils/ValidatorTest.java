package com.clearsolutions.usermanager.utils;

import com.clearsolutions.usermanager.exceptions.custom.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {

    private final int MINIMAL_AGE = 18;

    @DisplayName("Test validateUserAge method with valid age")
    @Test
    void testValidateUserAge_WithValidAge() {
        // Prepare
        var birthDate = LocalDate.of(1990, 1, 1);

        // Act & Assert
        assertDoesNotThrow(() -> Validator.validateUserAge(birthDate, MINIMAL_AGE));
    }

    @DisplayName("Test validateUserAge method with invalid age")
    @Test
    void testValidateUserAge_WithInvalidAge() {
        // Prepare
        var expectedErrorMessage = "User must be at least 18 years old.";
        var birthDate = LocalDate.of(2022, 1, 1);

        // Act & Assert
        var exception = assertThrows(ValidationException.class, () -> Validator.validateUserAge(birthDate, MINIMAL_AGE));
        assertTrue(exception.getMessage().contains(expectedErrorMessage));
    }
}
