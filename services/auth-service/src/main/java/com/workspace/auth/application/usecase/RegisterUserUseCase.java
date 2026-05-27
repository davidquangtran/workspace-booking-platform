package com.workspace.auth.application.usecase;

import com.workspace.auth.application.dto.request.RegisterRequest;
import com.workspace.auth.application.dto.response.AuthResult;
import com.workspace.auth.application.port.TokenPort;
import com.workspace.auth.application.port.PasswordEncoder;
import com.workspace.auth.application.port.TokenHasher;
import com.workspace.auth.domain.entity.RefreshToken;
import com.workspace.auth.domain.entity.User;
import com.workspace.auth.domain.exception.EmailAlreadyExistsException;
import com.workspace.auth.domain.repository.RefreshTokenRepository;
import com.workspace.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCase {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenPort tokenPort;
    private final TokenHasher tokenHasher;

    @Value("${auth.jwt.refresh-token-expiry}")
    private long refreshTokenExpiryMs;

    @Transactional
    public AuthResult execute(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        User user = User.create(request.email(), encodedPassword);

        User savedUser = userRepository.save(user);

        String accessToken = tokenPort.generateAccessToken(savedUser);

        String refreshToken = tokenPort.generateRefreshToken();

        String tokenHash = tokenHasher.sha256(refreshToken);
        RefreshToken newRefreshToken = RefreshToken.issue(savedUser.getId(),
                tokenHash,
                request.deviceInfo(),
                request.ipAddress(),
                Duration.ofMillis(refreshTokenExpiryMs));
        refreshTokenRepository.save(newRefreshToken);
        return new AuthResult(accessToken, refreshToken, tokenPort.getAccessTokenExpiresIn());
    }
}
