package com.workspace.auth.usecases.login;

import java.time.Instant;
import java.util.UUID;

public record LoginUserOutput(
        UUID userId,
        String email,
        String role,
        String accessToken,
        Instant accessTokenExpiresAt,
        String refreshToken,
        Instant refreshTokenExpiresAt
) {
}