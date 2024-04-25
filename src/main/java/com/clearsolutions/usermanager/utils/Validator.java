package com.clearsolutions.usermanager.utils;

import com.clearsolutions.usermanager.exceptions.custom.ValidationException;

import java.time.LocalDate;
import java.time.Period;

/**
 * Utility class for various validation operations related to user management.
 * This class provides methods to validate date ranges, user age, and other validation requirements.
 */
public class Validator {

    /**
     * Validates the date range to ensure that the 'from' date is before or equal to the 'to' date.
     *
     * @param from The start date of the range.
     * @param to   The end date of the range.
     * @throws ValidationException if the 'from' date is after the 'to' date.
     */
    public static void validateDateRange(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new ValidationException("The 'from' date must be before the 'to' date.");
        }
    }

    /**
     * Validates the age of a user to ensure that they are at least the specified minimum age.
     *
     * @param birthDate   The date of birth of the user.
     * @param minimumAge  The minimum age required for the user.
     * @throws ValidationException if the user's age is less than the specified minimum age.
     */
    public static void validateUserAge(LocalDate birthDate, int minimumAge) {
        var today = LocalDate.now();
        var period = Period.between(birthDate, today);

        if (period.getYears() < minimumAge) {
            throw new ValidationException("User must be at least " + minimumAge + " years old.");
        }
    }
}
