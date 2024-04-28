package com.clearsolutions.usermanager.dto.annotation;

import com.clearsolutions.usermanager.dto.DateOrderValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateOrderValidator.class)
public @interface DateOrder {
    String message() default "End date must be before start date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}