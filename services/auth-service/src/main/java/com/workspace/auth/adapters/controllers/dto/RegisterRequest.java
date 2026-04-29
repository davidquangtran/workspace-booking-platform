package com.workspace.auth.adapters.controllers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to register a new user")
public record RegisterRequest(

        @Schema(description = "User email (used as login identifier). Must be unique.",
                example = "alice@example.com")
        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email format is invalid")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        String email,

        @Schema(description = "Password. Must be 8-100 chars, at least one uppercase, one lowercase, one digit.",
                example = "Secret123",
                minLength = 8, maxLength = 100)
        @NotBlank(message = "Password must not be blank")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        String password
) {
}