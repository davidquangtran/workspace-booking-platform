package com.workspace.auth.application.usecase;

import com.workspace.auth.application.dto.request.LoginRequest;
import com.workspace.auth.application.dto.response.AuthResponse;
import com.workspace.auth.application.port.JwtPort;
import com.workspace.auth.application.port.PasswordEncoder;
import com.workspace.auth.domain.entity.User;
import com.workspace.auth.domain.exception.InvalidCredentialsException;
import com.workspace.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUseCase {
    private final UserRepository userRepository;
    private final JwtPort jwtPort;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse execute(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException());

        if(!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String accessToken = jwtPort.generateAccessToken(user);

        String refreshToken = jwtPort.generateRefreshToken(user);

        return AuthResponse.of(accessToken, refreshToken, jwtPort.getAccessTokenExpiresIn());
    }
}
