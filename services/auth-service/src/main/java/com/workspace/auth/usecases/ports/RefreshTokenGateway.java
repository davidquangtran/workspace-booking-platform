package com.workspace.auth.usecases.ports;

import com.workspace.auth.entities.RefreshToken;
import com.workspace.auth.entities.UserId;

import java.util.Optional;

public interface RefreshTokenGateway {

    RefreshToken save(RefreshToken token);

    Optional<RefreshToken> findByToken(String token);

    void revokeAllByUserId(UserId userId);

    /**
     * Mark a single refresh token as revoked.
     * Used by logout and refresh-with-rotation flows.
     */
    void revoke(RefreshToken token);
}