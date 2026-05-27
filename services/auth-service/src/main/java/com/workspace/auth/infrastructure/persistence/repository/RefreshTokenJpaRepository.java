package com.workspace.auth.infrastructure.persistence.repository;

import com.workspace.auth.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenJpaEntity, UUID> {

    Optional<RefreshTokenJpaEntity> findByTokenHash(String tokenHash);

    @Modifying
    @Query("""
        UPDATE RefreshTokenJpaEntity t
           SET t.revokedAt = :now
         WHERE t.userId = :userId
           AND t.familyTokenId = :familyTokenId
           AND t.revokedAt IS NULL
        """)
    int revokeAllFamilyToken(@Param("userId") UUID userId, @Param("familyTokenId") UUID familyTokenId, @Param("now")Instant now);

    @Modifying
    @Query("""
        UPDATE RefreshTokenJpaEntity t
           SET t.revokedAt = :now
         WHERE t.userId = :userId
           AND t.revokedAt IS NULL
        """)
    int revokeAllByUserId(@Param("userId") UUID userId, @Param("now")Instant now);
}
