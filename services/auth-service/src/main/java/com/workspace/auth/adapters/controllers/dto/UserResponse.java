package com.workspace.auth.adapters.controllers.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Information about a registered user")
public record UserResponse(

        @Schema(description = "Unique user ID (UUID)", example = "1d031569-86ae-481c-a737-895e392987f1")
        UUID id,

        @Schema(description = "User email", example = "alice@example.com")
        String email,

        @Schema(description = "User role", example = "USER", allowableValues = {"USER", "ADMIN"})
        String role,

        @Schema(description = "User status", example = "ACTIVE",
                allowableValues = {"ACTIVE", "SUSPENDED", "DELETED"})
        String status,

        @Schema(description = "Account creation timestamp")
        LocalDateTime createdAt
) {
}