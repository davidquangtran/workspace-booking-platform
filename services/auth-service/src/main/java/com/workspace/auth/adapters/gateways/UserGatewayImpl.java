package com.workspace.auth.adapters.gateways;

import com.workspace.auth.adapters.gateways.jpa.UserJpaEntity;
import com.workspace.auth.adapters.gateways.jpa.UserJpaMapper;
import com.workspace.auth.adapters.gateways.jpa.UserJpaRepository;
import com.workspace.auth.entities.Email;
import com.workspace.auth.entities.User;
import com.workspace.auth.entities.UserId;
import com.workspace.auth.usecases.ports.UserGateway;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserGatewayImpl implements UserGateway {

    private final UserJpaRepository jpaRepository;
    private final UserJpaMapper mapper;

    public UserGatewayImpl(UserJpaRepository jpaRepository, UserJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<User> findById(UserId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.value()).map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.value());
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = mapper.toJpaEntity(user);
        UserJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}