package com.workspace.auth.infrastructure.persistence.repository;

import com.workspace.auth.domain.entity.RefreshToken;
import com.workspace.auth.domain.repository.RefreshTokenRepository;
import com.workspace.auth.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import com.workspace.auth.infrastructure.persistence.mapper.RefreshTokenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {
    private final RefreshTokenJpaRepository repo;
    private final RefreshTokenMapper mapper;


    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenJpaEntity entity =
                repo.findById(refreshToken.getId())
                        .map(existing -> mapper.toUpdatedEntity(refreshToken, existing))
                        .orElseGet(() -> mapper.toJpaEntity(refreshToken));

        RefreshTokenJpaEntity saved = repo.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return repo.findByTokenHash(tokenHash).map(mapper::toDomain);
    }

    @Override
    public int revokeAllFamilyToken(UUID userId, UUID familyTokenId) {
        return repo.revokeAllFamilyToken(userId, familyTokenId, Instant.now());
    }

    @Override
    @Transactional
    public int revokeAllByUserId(UUID userId) {
        return repo.revokeAllByUserId(userId, Instant.now());
    }
}
