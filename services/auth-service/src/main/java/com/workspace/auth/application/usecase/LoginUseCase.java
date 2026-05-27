package com.workspace.auth.application.usecase;

import com.workspace.auth.application.dto.request.LoginRequest;
import com.workspace.auth.application.dto.response.AuthResult;
import com.workspace.auth.application.port.TokenPort;
import com.workspace.auth.application.port.PasswordEncoder;
import com.workspace.auth.application.port.TokenHasher;
import com.workspace.auth.domain.entity.RefreshToken;
import com.workspace.auth.domain.entity.User;
import com.workspace.auth.domain.exception.InvalidCredentialsException;
import com.workspace.auth.domain.repository.RefreshTokenRepository;
import com.workspace.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class LoginUseCase {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenPort tokenPort;
    private final PasswordEncoder passwordEncoder;
    private final TokenHasher tokenHasher;

    @Value("${auth.jwt.refresh-token-expiry}")
    private long refreshTokenExpiryMs;

    public AuthResult execute(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String accessToken = tokenPort.generateAccessToken(user);
        String refreshToken = tokenPort.generateRefreshToken();

        String tokenHash = tokenHasher.sha256(refreshToken);
        RefreshToken refreshTokenEntity = RefreshToken.issue(
                user.getId(), tokenHash, request.deviceInfo(), request.ipAddress(), Duration.ofMillis(refreshTokenExpiryMs)
        );

        refreshTokenRepository.save(refreshTokenEntity);

        return new AuthResult(accessToken, refreshToken, tokenPort.getAccessTokenExpiresIn());
    }
}
