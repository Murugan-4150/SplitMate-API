package com.splitmate.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhoneNumber {

    String message() default "Invalid phone number. Please use international format (e.g. +919876543210).";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
