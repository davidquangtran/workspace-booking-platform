package com.workspace.auth.entities;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class RefreshToken {
    private final UUID id;
    private final String token;
    private final UserId userId;
    private final Instant expiresAt;
    private boolean revoked;
    private final Instant createdAt;


    private RefreshToken(UUID id, String token, UserId userId, Instant expiresAt, boolean revoked, Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.token = Objects.requireNonNull(token);
        this.userId = Objects.requireNonNull(userId);
        this.expiresAt = Objects.requireNonNull(expiresAt);
        this.revoked = revoked;
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public static RefreshToken issue(UserId userId, String token, Instant expiresAt) {
        return new RefreshToken(
                UUID.randomUUID(), token, userId, expiresAt,
                false, Instant.now()
        );
    }

    public static RefreshToken reconstruct(UUID id, String token, UserId userId,
                                           Instant expiresAt, boolean revoked, Instant createdAt) {
        return new RefreshToken(id, token, userId, expiresAt, revoked, createdAt);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !revoked && !isExpired();
    }

    public void revoke() {
        this.revoked = true;
    }

    public UUID getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public UserId getUserId() {
        return userId;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

