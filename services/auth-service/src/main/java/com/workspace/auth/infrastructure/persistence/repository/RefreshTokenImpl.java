package com.workspace.auth.infrastructure.persistence.repository;

import com.workspace.auth.domain.entity.RefreshToken;
import com.workspace.auth.domain.repository.RefreshTokenRepository;
import com.workspace.auth.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import com.workspace.auth.infrastructure.persistence.mapper.RefreshTokenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RefreshTokenImpl implements RefreshTokenRepository {
    private final RefreshTokenJpaRepository repo;
    private final RefreshTokenMapper mapper;


    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenJpaEntity entity = mapper.toJpaEntity(refreshToken);
        RefreshTokenJpaEntity saved = repo.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<RefreshToken> findByUserId(UUID userId) {
        return repo.findByUserId(userId).map(mapper::toDomain);
    }
}
