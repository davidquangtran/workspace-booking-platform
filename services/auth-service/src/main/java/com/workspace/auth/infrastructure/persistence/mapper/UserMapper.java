package com.workspace.auth.infrastructure.persistence.mapper;

import com.workspace.auth.domain.entity.User;
import com.workspace.auth.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

// Manual mapper — không dùng MapStruct cho domain entity
// Lý do: domain entity immutable, không có setter
// MapStruct cần setter hoặc public builder → vi phạm encapsulation
@Component
public class UserMapper {

    // Domain → JPA entity (để lưu DB)
    public UserJpaEntity toJpaEntity(User user) {
        return UserJpaEntity.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }

    // JPA entity → Domain (để dùng trong business logic)
    // Dùng reconstitute — không cần validate lại vì data từ DB đã clean
    public User toDomain(UserJpaEntity entity) {
        return User.reconstitute(
                entity.getId(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getRole(),
                entity.isActive(),
                entity.getCreatedAt()
        );
    }

    public UserJpaEntity toUpdatedEntity(User domain, UserJpaEntity entity) {
        return UserJpaEntity.builder()
                .id(entity.getId())
                .email(domain.getEmail())
                .password(domain.getPassword())
                .role(domain.getRole())
                .isActive(domain.isActive())
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}