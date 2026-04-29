package com.workspace.auth.adapters.gateways.jpa;

import com.workspace.auth.entities.Email;
import com.workspace.auth.entities.HashedPassword;
import com.workspace.auth.entities.Role;
import com.workspace.auth.entities.User;
import com.workspace.auth.entities.UserId;
import com.workspace.auth.entities.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserJpaMapper {

    public UserJpaEntity toJpaEntity(User domain) {
        return new UserJpaEntity(
                domain.getId().value(),
                domain.getEmail().value(),
                domain.getPasswordHash().value(),
                domain.getRole().name(),
                domain.getStatus().name(),
                domain.getCreatedAt()
        );
    }

    public User toDomain(UserJpaEntity entity) {
        return User.reconstruct(
                UserId.of(entity.getId()),
                new Email(entity.getEmail()),
                new HashedPassword(entity.getPasswordHash()),
                Role.valueOf(entity.getRole()),
                UserStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt()
        );
    }
}