package com.workspace.auth.adapters.gateways.jpa;

import com.workspace.auth.entities.RefreshToken;
import com.workspace.auth.entities.UserId;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenJpaMapper {

    public RefreshTokenJpaEntity toJpaEntity(RefreshToken domain) {
        return new RefreshTokenJpaEntity(
                domain.getId(),
                domain.getToken(),
                domain.getUserId().value(),
                domain.getExpiresAt(),
                domain.isRevoked(),
                domain.getCreatedAt()
        );
    }

    public RefreshToken toDomain(RefreshTokenJpaEntity entity) {
        return RefreshToken.reconstruct(
                entity.getId(),
                entity.getToken(),
                UserId.of(entity.getUserId()),
                entity.getExpiresAt(),
                entity.isRevoked(),
                entity.getCreatedAt()
        );
    }
}