package com.workspace.auth.domain.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshToken {
    UUID id;
    UUID parentTokenId;
    UUID familyTokenId;
    UUID userId;
    String tokenHash;
    String deviceInfo;
    String ipAddress;
    Instant expiresAt;
    Instant revokedAt;
    Instant createdAt;

    @Builder(access = AccessLevel.PACKAGE)
    private RefreshToken(UUID id, UUID parentTokenId, UUID familyTokenId, String tokenHash, UUID userId, String deviceInfo, String ipAddress, Instant expiresAt, Instant revokedAt, Instant createdAt) {
        this.id = id;
        this.parentTokenId = parentTokenId;
        this.familyTokenId = familyTokenId;
        this.tokenHash = tokenHash;
        this.userId = userId;
        this.deviceInfo = deviceInfo;
        this.ipAddress = ipAddress;
        this.expiresAt = expiresAt;
        this.revokedAt = revokedAt;
        this.createdAt = createdAt;
    }


    public static RefreshToken issue(UUID userId, String tokenHash,
                                     String deviceInfo, String ipAddress,
                                     Duration ttl) {
        Instant now = Instant.now();
        UUID uuid = UUID.randomUUID();
        return RefreshToken.builder()
                .id(uuid)
                .parentTokenId(null)
                .familyTokenId(uuid)
                .userId(userId)
                .tokenHash(tokenHash)
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .createdAt(now)
                .expiresAt(now.plus(ttl))
                .build();
    }

    public static RefreshToken reconstitute(UUID id, UUID parentTokenId, UUID familyTokenId, UUID userId, String tokenHash,
                                            String deviceInfo, String ipAddress,
                                            Instant expiresAt, Instant revokedAt,
                                            Instant createdAt) {
        return RefreshToken.builder()
                .id(id)
                .parentTokenId(parentTokenId)
                .familyTokenId(familyTokenId)
                .userId(userId)
                .tokenHash(tokenHash)
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .expiresAt(expiresAt)
                .revokedAt(revokedAt)
                .createdAt(createdAt)
                .build();
    }

    public RefreshToken rotate(String newTokenHash, Duration ttl) {
        Instant now = Instant.now();
        return RefreshToken.builder()
                .id(UUID.randomUUID())
                .parentTokenId(this.id)
                .familyTokenId(this.familyTokenId)
                .userId(this.userId)
                .tokenHash(newTokenHash)
                .deviceInfo(this.deviceInfo)
                .ipAddress(this.ipAddress)
                .createdAt(now)
                .expiresAt(now.plus(ttl))
                .build();
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public boolean isValid() {
        return !isExpired() && !isRevoked();
    }

    public void revoke() {
        if (revokedAt != null) {
            return;
        }
        this.revokedAt = Instant.now();
    }
}
