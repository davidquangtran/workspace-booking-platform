package com.workspace.auth.usecases.ports;

import com.workspace.auth.entities.Email;
import com.workspace.auth.entities.User;
import com.workspace.auth.entities.UserId;

import java.util.Optional;

public interface UserGateway {

    Optional<User> findById(UserId id);

    Optional<User> findByEmail(Email email);

    boolean existsByEmail(Email email);

    User save(User user);
}
