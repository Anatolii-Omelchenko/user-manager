
package com.clearsolutions.usermanager.dto;

import com.clearsolutions.usermanager.dto.annotation.DateOrder;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateOrderValidator implements ConstraintValidator<DateOrder, DateRange> {

    @Override
    public boolean isValid(DateRange dateRange, ConstraintValidatorContext context) {
        if (dateRange.from() == null || dateRange.to() == null) return true;

        return dateRange.from().isBefore(dateRange.to()) || dateRange.from().equals(dateRange.to());
    }
}
