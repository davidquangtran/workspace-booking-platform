package com.workspace.auth.application.dto.response;

/**
 * Kết quả internal của login/register/refresh use cases.
 * Controller sẽ chuyển refresh token vào cookie, access token + expiresIn vào response body.
 */
public record AuthResult(
        String accessToken,
        String refreshToken,    // plaintext, sẽ vào cookie
        long expiresIn
) { }