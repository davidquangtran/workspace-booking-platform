package com.workspace.auth.domain.repository;

import com.workspace.auth.domain.entity.User;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findByIdAndIsActive(UUID id,boolean isActive);

    boolean existsByEmail(String email);


}
