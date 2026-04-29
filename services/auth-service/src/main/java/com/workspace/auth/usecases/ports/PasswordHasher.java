package com.workspace.auth.usecases.ports;

import com.workspace.auth.entities.HashedPassword;

public interface PasswordHasher {

    HashedPassword hash(String rawPassword);

    boolean matches(String rawPassword, HashedPassword hashed);
}