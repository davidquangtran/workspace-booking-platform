package com.workspace.auth.application.dto.response;

// Data trả về sau khi login/register thành công
public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn    // seconds
) {
    // Factory method — tạo response chuẩn
    public static AuthResponse of(String accessToken, String refreshToken, long expiresIn) {
        return new AuthResponse(accessToken, refreshToken, "Bearer", expiresIn);
    }
}