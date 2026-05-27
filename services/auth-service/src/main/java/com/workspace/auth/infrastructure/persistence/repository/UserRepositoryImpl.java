package com.workspace.auth.infrastructure.persistence.repository;

import com.workspace.auth.domain.entity.User;
import com.workspace.auth.domain.repository.UserRepository;
import com.workspace.auth.infrastructure.persistence.entity.UserJpaEntity;
import com.workspace.auth.infrastructure.persistence.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository repo;
    private final UserMapper mapper;


    @Override
    public User save(User user) {
        UserJpaEntity entity =
                repo.findById(user.getId())
                        .map(existing -> mapper.toUpdatedEntity(user, existing))
                        .orElseGet(() -> mapper.toJpaEntity(user));
        UserJpaEntity saved = repo.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repo.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByIdAndIsActive(UUID id, boolean isActive) {
        return repo.findByIdAndIsActive(id, isActive).map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repo.existsByEmail(email);
    }
}
