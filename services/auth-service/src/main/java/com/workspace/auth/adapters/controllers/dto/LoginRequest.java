package com.workspace.auth.adapters.controllers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to login with email and password")
public record LoginRequest(

        @Schema(description = "Email of the registered user", example = "alice@example.com")
        @NotBlank(message = "Email is required")
        @Email(message = "Email format is invalid")
        String email,

        @Schema(description = "Plain text password", example = "Secret123")
        @NotBlank(message = "Password is required")
        String password
) {
}