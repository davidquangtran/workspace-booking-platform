package com.workspace.auth.adapters.gateways;

import com.workspace.auth.adapters.gateways.jpa.RefreshTokenJpaEntity;
import com.workspace.auth.adapters.gateways.jpa.RefreshTokenJpaMapper;
import com.workspace.auth.adapters.gateways.jpa.RefreshTokenJpaRepository;
import com.workspace.auth.entities.RefreshToken;
import com.workspace.auth.entities.UserId;
import com.workspace.auth.usecases.ports.RefreshTokenGateway;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RefreshTokenGatewayImpl implements RefreshTokenGateway {

    private final RefreshTokenJpaRepository jpaRepository;
    private final RefreshTokenJpaMapper mapper;

    public RefreshTokenGatewayImpl(RefreshTokenJpaRepository jpaRepository, RefreshTokenJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public RefreshToken save(RefreshToken token) {
        RefreshTokenJpaEntity entity = mapper.toJpaEntity(token);
        RefreshTokenJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return jpaRepository.findByToken(token).map(mapper::toDomain);
    }

    @Override
    public void revokeAllByUserId(UserId userId) {
        jpaRepository.revokeAllByUserId(userId.value());
    }

    @Override
    public void revoke(RefreshToken token) {
        token.revoke();   // Domain logic: set revoked = true
        RefreshTokenJpaEntity entity = mapper.toJpaEntity(token);
        jpaRepository.save(entity);
    }
}