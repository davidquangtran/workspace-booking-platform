package com.workspace.auth.usecases.ports;

import com.workspace.auth.entities.User;

import java.time.Instant;

public interface TokenGenerator {

    /**
     * Generate signed access token containing user identity + role.
     */
    GeneratedToken generateAccessToken(User user);

    /**
     * Generate opaque refresh token (random string, not JWT).
     */
    GeneratedToken generateRefreshToken();

    record GeneratedToken(String value, Instant expiresAt) {
    }
}