package com.workspace.auth.entities;

import java.util.Objects;

public record HashedPassword(String value) {

    public HashedPassword {
        Objects.requireNonNull(value, "Hashed password must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Hashed password must not be blank");
        }
        if (value.length() < 20) {
            throw new IllegalArgumentException("Hashed password seems too short to be a valid hash. " +
                    "Did you forget to hash the raw password?");
        }
    }
}
