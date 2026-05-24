package com.workspace.auth.domain.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class RefreshToken {
    UUID id;
    UUID userId;
    String tokenHash;
    Boolean isActive;
    LocalDateTime createdAt;

    public RefreshToken(UUID id, UUID userId, String tokenHash, Boolean isActive, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    public static RefreshToken create(UUID userId, String tokenHash){
        return RefreshToken.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .tokenHash(tokenHash)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void revoke() {
        this.isActive = false;
    }

}
