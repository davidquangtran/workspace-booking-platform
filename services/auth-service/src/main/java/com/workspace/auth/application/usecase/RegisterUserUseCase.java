package com.workspace.auth.application.usecase;

import com.workspace.auth.application.dto.request.RegisterRequest;
import com.workspace.auth.application.dto.response.AuthResponse;
import com.workspace.auth.application.port.JwtPort;
import com.workspace.auth.application.port.PasswordEncoder;
import com.workspace.auth.domain.entity.User;
import com.workspace.auth.domain.exception.EmailAlreadyExistsException;
import com.workspace.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtPort jwtPort;

    @Transactional
    public AuthResponse execute(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        User user = User.create(request.email(), encodedPassword);

        userRepository.save(user);

        String accessToken = jwtPort.generateAccessToken(user);

        String refreshToken = jwtPort.generateRefreshToken(user);

        return AuthResponse.of(accessToken, refreshToken, jwtPort.getAccessTokenExpiresIn());
    }
}
