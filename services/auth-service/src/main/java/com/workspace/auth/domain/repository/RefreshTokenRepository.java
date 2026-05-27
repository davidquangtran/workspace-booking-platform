package com.workspace.auth.domain.repository;

import com.workspace.auth.domain.entity.RefreshToken;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {
    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    int revokeAllFamilyToken(UUID userId, UUID familyTokenId);

    int revokeAllByUserId(UUID userId);
}
