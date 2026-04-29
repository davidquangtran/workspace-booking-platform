package com.workspace.auth.adapters.controllers.dto;

import java.time.Instant;
import java.util.UUID;

public record LoginResponse(
        UUID userId,
        String email,
        String role,
        TokenInfo accessToken,
        TokenInfo refreshToken
) {
    public record TokenInfo(
            String token,
            Instant expiresAt
    ) {
    }
}