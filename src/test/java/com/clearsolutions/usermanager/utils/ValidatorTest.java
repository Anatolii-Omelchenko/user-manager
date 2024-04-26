package com.clearsolutions.usermanager.utils;

import com.clearsolutions.usermanager.exceptions.custom.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {

    @DisplayName("Test validateDateRange method with valid date range")
    @Test
    void testValidateDateRange_WithValidRange() {
        // Prepare
        var from = LocalDate.of(2020, 1, 1);
        var to = LocalDate.of(2021, 1, 1);

        // Act & Assert
        assertDoesNotThrow(() -> Validator.validateDateRange(from, to));
    }

    @DisplayName("Test validateDateRange method with invalid date range")
    @Test
    void testValidateDateRange_WithInvalidRange() {
        // Prepare
        var expectedErrorMessage = "The 'from' date must be before the 'to' date.";
        var from = LocalDate.of(2021, 1, 1);
        var to = LocalDate.of(2020, 1, 1);

        // Act & Assert
        var exception = assertThrows(ValidationException.class, () -> Validator.validateDateRange(from, to));
        assertTrue(exception.getMessage().contains(expectedErrorMessage));
    }

    @DisplayName("Test validateUserAge method with valid age")
    @Test
    void testValidateUserAge_WithValidAge() {
        // Prepare
        var birthDate = LocalDate.of(1990, 1, 1);
        int minimumAge = 18;

        // Act & Assert
        assertDoesNotThrow(() -> Validator.validateUserAge(birthDate, minimumAge));
    }

    @DisplayName("Test validateUserAge method with invalid age")
    @Test
    void testValidateUserAge_WithInvalidAge() {
        // Prepare
        var expectedErrorMessage = "User must be at least 18 years old.";
        var birthDate = LocalDate.of(2022, 1, 1);
        int minimumAge = 18;

        // Act & Assert
        var exception = assertThrows(ValidationException.class, () -> Validator.validateUserAge(birthDate, minimumAge));
        assertTrue(exception.getMessage().contains(expectedErrorMessage));
    }
}
