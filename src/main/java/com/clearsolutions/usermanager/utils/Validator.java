package com.clearsolutions.usermanager.utils;

import com.clearsolutions.usermanager.exceptions.custom.ValidationException;

import java.time.LocalDate;
import java.time.Period;

public class Validator {

    public static void validateDateRange(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new ValidationException("The 'from' date must be before the 'to' date.");
        }
    }

    public static void validateUserAge(LocalDate birthDate, int minimumAge) {
        var today = LocalDate.now();
        var period = Period.between(birthDate, today);

        if (period.getYears() < minimumAge) {
            throw new ValidationException("User must be at least 18 years old.");
        }
    }
}
