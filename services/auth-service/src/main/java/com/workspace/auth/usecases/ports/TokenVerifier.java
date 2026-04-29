package com.workspace.auth.usecases.ports;

import com.workspace.auth.entities.UserId;

public interface TokenVerifier {

    /**
     * Verify access token and extract claims.
     *
     * @throws TokenVerificationException if token is invalid (expired, malformed, bad signature)
     */
    VerifiedToken verify(String token);

    record VerifiedToken(
            UserId userId,
            String email,
            String role
    ) {
    }

    class TokenVerificationException extends RuntimeException {
        public TokenVerificationException(String message) {
            super(message);
        }

        public TokenVerificationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}