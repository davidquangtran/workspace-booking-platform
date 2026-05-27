package com.workspace.auth.infrastructure.persistence.repository;

import com.workspace.auth.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {
    Optional<UserJpaEntity> findByEmail(String email);

    Optional<UserJpaEntity> findByIdAndIsActive(UUID userId, boolean isActive);

    boolean existsByEmail(String email);
}
