package com.workspace.auth.application.usecase;

import com.workspace.auth.application.port.JwtPort;
import com.workspace.auth.domain.entity.RefreshToken;
import com.workspace.auth.domain.entity.User;
import com.workspace.auth.domain.repository.RefreshTokenRepository;
import com.workspace.auth.domain.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class LogoutUseCase {
    private final UserRepository userRepo;
    private final RefreshTokenRepository refreshTokenRepo;
    private final JwtPort jwtPort;

    public void logout(String accessToken){
        UUID userId =  jwtPort.extractUserId(accessToken);
        User user = userRepo.findByIdAndIsActive(userId,true).orElseThrow(()-> new EntityNotFoundException("Can't find user with ID:"+userId));
        RefreshToken refreshToken = refreshTokenRepo.findByUserId(user.getId()).orElseThrow(()-> new EntityNotFoundException("Can't find refresh token"));
        refreshToken.revoke();
        refreshTokenRepo.save(refreshToken);
    }
}
