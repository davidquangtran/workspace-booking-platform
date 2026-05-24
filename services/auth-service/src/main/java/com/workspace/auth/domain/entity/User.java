package com.workspace.auth.domain.entity;

import com.workspace.auth.domain.exception.InvalidEmailException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    UUID id;
    String email;
    String password;
    Role role;
    boolean isActive;
    LocalDateTime createdAt;

    @Builder(access = AccessLevel.PACKAGE)
    private User(UUID id, String email, String password,
                 Role role, boolean isActive, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    public static User create(String email, String encodedPassword) {
        validateEmail(email);

        return User.builder()
                .id(UUID.randomUUID())
                .email(email.toLowerCase().trim())
                .password(encodedPassword)
                .role(Role.CUSTOMER)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // Tái tạo User TỪ DB — không validate, data đã clean sẵn
// Đặt tên reconstitute để phân biệt với create
    public static User reconstitute(UUID id, String email, String password,
                                    Role role, boolean isActive, LocalDateTime createdAt) {
        return User.builder()
                .id(id)
                .email(email)
                .password(password)
                .role(role)
                .isActive(isActive)
                .createdAt(createdAt)
                .build();
    }

    public static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new InvalidEmailException("Email must not be blank.");
        }
        if (!email.contains("@") || !email.contains(".")) {
            throw new InvalidEmailException("Invalid email: " + email);
        }
    }
}
