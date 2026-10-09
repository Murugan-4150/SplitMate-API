package com.splitmate.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(

        @NotBlank(message = "Email or phone number is required.")
        @Size(max = 255, message = "Email or phone must not exceed 255 characters.")
        String emailOrPhone,

        @NotBlank(message = "Password is required.")
        @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters.")
        String password

) {
}
