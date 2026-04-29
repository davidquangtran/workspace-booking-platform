package com.workspace.auth.usecases.refresh;

import java.time.Instant;

public record RefreshAccessTokenOutput(
        String accessToken,
        Instant accessTokenExpiresAt,
        String refreshToken,
        Instant refreshTokenExpiresAt
) {
}