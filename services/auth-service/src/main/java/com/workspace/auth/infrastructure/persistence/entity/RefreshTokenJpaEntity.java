package com.workspace.auth.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class RefreshTokenJpaEntity {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    UUID id;

    @Column(name = "user_id",nullable = false)
    UUID userId;

    @Column(name = "token_hash", nullable = false, length = 3000)
    String tokenHash;

    @Column(name = "is_active",nullable = false)
    Boolean isActive;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @Version
    @Column(name = "version", nullable = false)
    Long version;


}
