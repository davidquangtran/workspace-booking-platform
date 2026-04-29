package com.workspace.auth.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class User {
    private final UserId id;
    private final Email email;
    private final HashedPassword passwordHash;
    private Role role;
    private UserStatus status;
    private final LocalDateTime createdAt;


    public User(UserId id, Email email, HashedPassword passwordHash, Role role, UserStatus status, LocalDateTime createdAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.email = Objects.requireNonNull(email, "email");
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash");
        this.role = Objects.requireNonNull(role, "role");
        this.status = Objects.requireNonNull(status, "status");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    /**
     * Factory method to register a new user.
     * Domain rule: new user always starts with role USER and status ACTIVE.
     */
    public static User register(Email email, HashedPassword passwordHash) {
        return new User(UserId.generate(), email, passwordHash, Role.USER, UserStatus.ACTIVE, LocalDateTime.now());
    }

    /**
     * Factory method to reconstruct User from persistence (load from DB).
     * Used by adapter layer, not by business logic.
     */
    public static User reconstruct(UserId id, Email email, HashedPassword passwordHash,
                                   Role role, UserStatus status, LocalDateTime createdAt) {
        return new User(id, email, passwordHash, role, status, createdAt);
    }

    // Domain logic — RICH DOMAIN MODEL

    public boolean canLogin() {
        return status == UserStatus.ACTIVE;
    }

    public void suspend() {
        if (this.status == UserStatus.SUSPENDED) {
            throw new IllegalStateException("User is already suspended");
        }
        if (this.status == UserStatus.DELETED) {
            throw new IllegalStateException("Cannot suspend a deleted user");
        }
        this.status = UserStatus.SUSPENDED;
    }

    public void promoteToAdmin() {
        if (this.role == Role.ADMIN) {
            throw new IllegalStateException("User is already an admin");
        }
        this.role = Role.ADMIN;
    }

    // Getters (KHÔNG có setter — tránh sửa state tùy tiện)

    public UserId getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public HashedPassword getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
