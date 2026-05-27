package com.workspace.auth.infrastructure.persistence.mapper;

import com.workspace.auth.domain.entity.RefreshToken;
import com.workspace.auth.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenMapper {

    public RefreshTokenJpaEntity toJpaEntity(RefreshToken domain) {
        return RefreshTokenJpaEntity.builder()
                .id(domain.getId())
                .parentTokenId(domain.getParentTokenId())
                .familyTokenId(domain.getFamilyTokenId())
                .userId(domain.getUserId())
                .tokenHash(domain.getTokenHash())
                .deviceInfo(domain.getDeviceInfo())
                .ipAddress(domain.getIpAddress())
                .expiresAt(domain.getExpiresAt())
                .revokedAt(domain.getRevokedAt())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    public RefreshToken toDomain(RefreshTokenJpaEntity entity) {
        return RefreshToken.reconstitute(
                entity.getId(),
                entity.getParentTokenId(),
                entity.getFamilyTokenId(),
                entity.getUserId(),
                entity.getTokenHash(),
                entity.getDeviceInfo(),
                entity.getIpAddress(),
                entity.getExpiresAt(),
                entity.getRevokedAt(),
                entity.getCreatedAt()
        );
    }

    public RefreshTokenJpaEntity toUpdatedEntity(
            RefreshToken domain,
            RefreshTokenJpaEntity entity
    ) {
        return RefreshTokenJpaEntity.builder()
                .id(entity.getId())
                .parentTokenId(domain.getParentTokenId())
                .familyTokenId(domain.getFamilyTokenId())
                .userId(domain.getUserId())
                .tokenHash(domain.getTokenHash())
                .deviceInfo(domain.getDeviceInfo())
                .ipAddress(domain.getIpAddress())
                .expiresAt(domain.getExpiresAt())
                .revokedAt(domain.getRevokedAt())
                .createdAt(entity.getCreatedAt())
                .version(entity.getVersion())
                .build();
    }
}
