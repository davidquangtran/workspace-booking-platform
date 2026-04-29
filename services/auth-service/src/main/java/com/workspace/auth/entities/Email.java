package com.workspace.auth.entities;

import com.workspace.auth.entities.exception.InvalidEmailException;

import java.util.Objects;
import java.util.regex.Pattern;

public record Email(String value) {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public Email {
        Objects.requireNonNull(value, "Email must not be null");
        String normalized = value.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new InvalidEmailException(value);
        }
        value = normalized;
    }
}
