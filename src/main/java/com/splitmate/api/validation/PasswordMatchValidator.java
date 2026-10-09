package com.splitmate.api.validation;

import com.splitmate.api.dto.request.RegisterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, RegisterRequest> {

    @Override
    public boolean isValid(RegisterRequest request, ConstraintValidatorContext context) {
        if (request.password() == null || request.confirmPassword() == null) {
            return true;
        }

        boolean matches = request.password().equals(request.confirmPassword());

        if (!matches) {
            // Bind the error to the confirmPassword field instead of the class level
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Passwords do not match.")
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation();
        }

        return matches;
    }
}
