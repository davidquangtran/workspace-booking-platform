package com.workspace.auth.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_refresh_tokens_family_token_id", columnList = "family_token_id"),
                @Index(name = "idx_refresh_tokens_token_hash", columnList = "token_hash"),
                @Index(name = "idx_refresh_tokens_user_id", columnList = "user_id"),
                @Index(name = "idx_refresh_tokens_expires_at", columnList = "expires_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class RefreshTokenJpaEntity {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    UUID id;

    @Column(name = "parent_token_id")
    UUID parentTokenId;

    @Column(name = "family_token_id", nullable = false)
    UUID familyTokenId;

    @Column(name = "user_id", nullable = false)
    UUID userId;

    @Column(name = "token_hash", nullable = false, length = 64, unique = true)
    String tokenHash;

    @Column(name = "device_info", length = 500)
    String deviceInfo;

    @Column(name = "ip_address", length = 45)
    String ipAddress;

    @Column(name = "expires_at", nullable = false)
    Instant expiresAt;

    @Column(name = "revoked_at")
    Instant revokedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    Instant createdAt;

    @Version
    @Column(name = "version", nullable = false)
    Long version;
}
