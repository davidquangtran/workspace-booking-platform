package com.workspace.auth.domain.repository;

import com.workspace.auth.domain.entity.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {
    RefreshToken save(RefreshToken refreshToken);
    Optional<RefreshToken> findByUserId(UUID userId);
}
