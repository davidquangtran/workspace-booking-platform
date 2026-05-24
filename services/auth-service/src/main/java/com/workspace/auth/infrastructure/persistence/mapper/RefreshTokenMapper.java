package com.workspace.auth.infrastructure.persistence.mapper;

import com.workspace.auth.domain.entity.RefreshToken;
import com.workspace.auth.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenMapper {

    public RefreshTokenJpaEntity toJpaEntity(RefreshToken refreshToken) {
        return RefreshTokenJpaEntity.builder()
                .id(refreshToken.getId())
                .userId(refreshToken.getUserId())
                .tokenHash(refreshToken.getTokenHash())
                .isActive(refreshToken.getIsActive())
                .createdAt(refreshToken.getCreatedAt())
                .build();
    }

    public RefreshToken toDomain(RefreshTokenJpaEntity entity){
        return RefreshToken.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .tokenHash(entity.getTokenHash())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .build();
    }

}
