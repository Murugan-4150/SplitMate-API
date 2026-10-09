package com.splitmate.api.dto.request;

import com.splitmate.api.validation.PasswordMatch;
import com.splitmate.api.validation.ValidPhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@PasswordMatch
public record RegisterRequest(

        @NotBlank(message = "Full name is required.")
        @Size(min = 2, max = 50, message = "Full name must be between 2 and 50 characters.")
        String fullName,

        @NotBlank(message = "Email address is required.")
        @Email(message = "Please enter a valid email address.")
        @Size(max = 255, message = "Email must not exceed 255 characters.")
        String email,

        @NotBlank(message = "Phone number is required.")
        @ValidPhoneNumber
        String phoneNumber,

        @NotBlank(message = "Password is required.")
        @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
                message = "Password must contain at least 1 uppercase letter, 1 lowercase letter, and 1 number."
        )
        String password,

        @NotBlank(message = "Please confirm your password.")
        String confirmPassword

) {
}
