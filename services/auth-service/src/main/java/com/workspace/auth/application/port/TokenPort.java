package com.workspace.auth.application.port;

import com.workspace.auth.domain.entity.User;

import java.util.UUID;

// UseCase cần tạo token
// Nhưng không biết JWT lib nào đang được dùng
public interface TokenPort {

    String generateAccessToken(User user);

    String generateRefreshToken();

    long getAccessTokenExpiresIn();   // seconds

    UUID extractUserId(String accessToken);
}