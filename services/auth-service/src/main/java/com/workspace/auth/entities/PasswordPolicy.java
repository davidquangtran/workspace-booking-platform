package com.workspace.auth.entities;

import com.workspace.auth.entities.exception.WeakPasswordException;

import java.util.regex.Pattern;

/**
 * Domain Service: Password policy rules.
 * Not bound to a specific entity — it's a stateless rule checker.
 *
 * Pure POJO — no Spring, no framework.
 */
public class PasswordPolicy {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 100;
    private static final Pattern HAS_UPPERCASE = Pattern.compile(".*[A-Z].*");
    private static final Pattern HAS_LOWERCASE = Pattern.compile(".*[a-z].*");
    private static final Pattern HAS_DIGIT = Pattern.compile(".*\\d.*");

    public void validate(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new WeakPasswordException("Password must not be blank");
        }
        if (rawPassword.length() < MIN_LENGTH) {
            throw new WeakPasswordException(
                    "Password must be at least " + MIN_LENGTH + " characters"
            );
        }
        if (rawPassword.length() > MAX_LENGTH) {
            throw new WeakPasswordException(
                    "Password must be at most " + MAX_LENGTH + " characters"
            );
        }
        if (!HAS_UPPERCASE.matcher(rawPassword).matches()) {
            throw new WeakPasswordException("Password must contain at least one uppercase letter");
        }
        if (!HAS_LOWERCASE.matcher(rawPassword).matches()) {
            throw new WeakPasswordException("Password must contain at least one lowercase letter");
        }
        if (!HAS_DIGIT.matcher(rawPassword).matches()) {
            throw new WeakPasswordException("Password must contain at least one digit");
        }
    }
}