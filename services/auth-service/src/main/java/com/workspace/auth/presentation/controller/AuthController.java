package com.workspace.auth.presentation.controller;

import com.workspace.auth.application.dto.request.LoginRequest;
import com.workspace.auth.application.dto.request.RefreshRequest;
import com.workspace.auth.application.dto.request.RegisterRequest;
import com.workspace.auth.application.dto.response.AuthResponse;
import com.workspace.auth.application.dto.response.AuthResult;
import com.workspace.auth.application.usecase.LoginUseCase;
import com.workspace.auth.application.usecase.LogoutUseCase;
import com.workspace.auth.application.usecase.RefreshAccessTokenUseCase;
import com.workspace.auth.application.usecase.RegisterUserUseCase;
import com.workspace.auth.presentation.dto.LoginHttpRequest;
import com.workspace.auth.presentation.dto.RegisterHttpRequest;
import com.workspace.auth.presentation.util.CookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login and Register")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final RefreshAccessTokenUseCase refreshAccessTokenUseCase;
    private final CookieUtil cookieUtil;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register new User")
    public AuthResponse register(@Valid @RequestBody RegisterHttpRequest request, HttpServletRequest httpReq, HttpServletResponse httpRes) {
        RegisterRequest dto = new RegisterRequest(
                request.email(),
                request.password(),
                httpReq.getHeader("User-Agent"),   // ← deviceInfo
                getClientIp(httpReq)             // ← ipAddress
        );
        AuthResult result = registerUserUseCase.execute(dto);
        cookieUtil.setRefreshTokenCookie(httpRes, result.refreshToken());
        return AuthResponse.of(result.accessToken(), "access", result.expiresIn());
    }

    @PostMapping("/login")
    @Operation(summary = "Login")
    public AuthResponse login(
            @Valid @RequestBody LoginHttpRequest request, HttpServletRequest httpReq, HttpServletResponse httpRes) {

        LoginRequest dto = new LoginRequest(
                request.email(),
                request.password(),
                httpReq.getHeader("User-Agent"),   // ← deviceInfo
                getClientIp(httpReq)             // ← ipAddress
        );
        AuthResult result = loginUseCase.execute(dto);
        cookieUtil.setRefreshTokenCookie(httpRes, result.refreshToken());
        return AuthResponse.of(result.accessToken(), "access", result.expiresIn());
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@CookieValue(value = "refresh_token", required = false) String refreshToken, HttpServletRequest httpReq, HttpServletResponse httpRes) {
        RefreshRequest dto = new RefreshRequest(httpReq.getHeader("User-Agent"), getClientIp(httpReq));
        AuthResult result = refreshAccessTokenUseCase.execute(refreshToken, dto);
        cookieUtil.setRefreshTokenCookie(httpRes, result.refreshToken());
        return AuthResponse.of(result.accessToken(), "access", result.expiresIn());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(value = "refresh_token", required = false) String refreshToken,
                                       HttpServletResponse httpResp) {
        logoutUseCase.execute(refreshToken);
        cookieUtil.clearRefreshTokenCookie(httpResp);
        return ResponseEntity.noContent().build();
    }

    private String getClientIp(HttpServletRequest req) {
        String forwarded = req.getHeader("X-Forwarded-For");
        if (forwarded != null) {
            // Header có thể là chuỗi nhiều IP "client, proxy1, proxy2" — lấy IP đầu tiên
            return forwarded.split(",")[0].trim();
        }
        return req.getRemoteAddr();
    }
}
