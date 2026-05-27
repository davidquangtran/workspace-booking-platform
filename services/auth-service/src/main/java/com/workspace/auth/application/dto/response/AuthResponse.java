package com.workspace.auth.application.dto.response;

// Data trả về sau khi login/register thành công
public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn    // seconds
) {
    // Factory method — tạo response chuẩn
    public static AuthResponse of(String accessToken, String tokenType, long expiresIn) {
        return new AuthResponse(accessToken, tokenType, expiresIn);
    }
}