package com.workspace.auth.application.port;

// UseCase cần encode/verify password
// Nhưng UseCase không biết BCrypt hay gì — chỉ biết interface này
public interface PasswordEncoder {

    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}