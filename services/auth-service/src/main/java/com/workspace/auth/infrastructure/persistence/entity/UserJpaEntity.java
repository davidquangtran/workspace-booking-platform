package com.workspace.auth.infrastructure.persistence.entity;

import com.workspace.auth.domain.entity.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserJpaEntity {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    UUID id;

    @Column(name = "email", unique = true, nullable = false, length = 255)
    String email;

    @Column(name = "password", nullable = false)
    String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    Role role;

    @Column(name = "is_active", nullable = false)
    boolean isActive;

    @Column(name = "created_at", updatable = false)
    Instant createdAt;

    @Version
    @Column(name = "version", nullable = false)
    Long version;
}
