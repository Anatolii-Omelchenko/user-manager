package com.clearsolutions.usermanager.dto;

import com.clearsolutions.usermanager.dto.annotation.OnlyAdult;
import com.clearsolutions.usermanager.properties.ValidationProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
@RequiredArgsConstructor
public class AdultValidator implements ConstraintValidator<OnlyAdult, LocalDate> {

    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext context) {
        if (birthDate == null) return false;
        var today = LocalDate.now();
        var period = Period.between(birthDate, today);

        return period.getYears() >= ValidationProperties.getMinimalAge();
    }
}
