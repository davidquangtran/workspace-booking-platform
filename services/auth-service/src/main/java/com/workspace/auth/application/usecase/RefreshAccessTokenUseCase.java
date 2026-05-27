package com.workspace.auth.application.usecase;

import com.workspace.auth.application.dto.request.RefreshRequest;
import com.workspace.auth.application.dto.response.AuthResult;
import com.workspace.auth.application.port.TokenPort;
import com.workspace.auth.application.port.TokenHasher;
import com.workspace.auth.domain.entity.RefreshToken;
import com.workspace.auth.domain.entity.User;
import com.workspace.auth.domain.exception.InvalidCredentialsException;
import com.workspace.auth.domain.repository.RefreshTokenRepository;
import com.workspace.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshAccessTokenUseCase {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final TokenPort tokenPort;
    private final TokenHasher tokenHasher;

    @Value("${auth.jwt.refresh-token-expiry}")
    private long refreshTokenExpiryMs;

    @Transactional()
    public AuthResult execute(String refreshToken, RefreshRequest request) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidCredentialsException();
        }

        String tokenHash = tokenHasher.sha256(refreshToken);
        RefreshToken recentRefreshToken = refreshTokenRepository.findByTokenHash(tokenHash).orElseThrow(InvalidCredentialsException::new);

        if (recentRefreshToken.isExpired()) {
            throw new InvalidCredentialsException();
        }

        if (recentRefreshToken.isRevoked()) {
            refreshTokenRepository.revokeAllFamilyToken(recentRefreshToken.getUserId(), recentRefreshToken.getFamilyTokenId());
            throw new InvalidCredentialsException();
        }

        User user = userRepository.findByIdAndIsActive(recentRefreshToken.getUserId(), true).orElseThrow(InvalidCredentialsException::new);

        String newAccessToken = tokenPort.generateAccessToken(user);
        String newRefreshToken = tokenPort.generateRefreshToken();

        recentRefreshToken.revoke();
        refreshTokenRepository.save(recentRefreshToken);

        RefreshToken rotateRefreshToken = recentRefreshToken.rotate(
                tokenHasher.sha256(newRefreshToken),
                Duration.ofMillis(refreshTokenExpiryMs)
        );
        refreshTokenRepository.save(rotateRefreshToken);
        return new AuthResult(newAccessToken, newRefreshToken, tokenPort.getAccessTokenExpiresIn());

    }
}
