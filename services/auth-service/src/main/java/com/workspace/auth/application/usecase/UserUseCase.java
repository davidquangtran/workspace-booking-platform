package com.workspace.auth.application.usecase;

import com.workspace.auth.application.dto.response.UserResponse;
import com.workspace.auth.application.port.JwtPort;
import com.workspace.auth.domain.entity.User;
import com.workspace.auth.domain.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserUseCase {
    private final UserRepository repo;
    private final JwtPort jwtPort;

    public UserResponse getInfo(String accessToken) {
        UUID userId = jwtPort.extractUserId(accessToken);
        User user = repo.findByIdAndIsActive(userId,true).orElseThrow(()->new EntityNotFoundException("User not found"));
        return UserResponse.of(user.getEmail(), user.getRole());
    }


}
