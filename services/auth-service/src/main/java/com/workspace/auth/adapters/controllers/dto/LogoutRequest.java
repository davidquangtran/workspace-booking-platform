package com.workspace.auth.adapters.controllers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to logout (revoke refresh token)")
public record LogoutRequest(

        @Schema(description = "The refresh token to revoke",
                example = "31cfb3a2-399d-4090-b67b-e65039e44236-d5f78307-c972-435c-ba1b-ba2b31872bd7")
        @NotBlank(message = "Refresh token is required")
        String refreshToken
) {
}