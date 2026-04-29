package com.workspace.auth.adapters.controllers.dto;

import java.time.Instant;

public record RefreshTokenResponse(
        TokenInfo accessToken,
        TokenInfo refreshToken
) {
    public record TokenInfo(String token, Instant expiresAt) {
    }
}