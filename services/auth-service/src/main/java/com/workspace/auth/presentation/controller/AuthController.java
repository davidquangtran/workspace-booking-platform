package com.workspace.auth.presentation.controller;

import com.workspace.auth.application.dto.request.LoginRequest;
import com.workspace.auth.application.dto.request.RegisterRequest;
import com.workspace.auth.application.dto.response.AuthResponse;
import com.workspace.auth.application.usecase.LoginUseCase;
import com.workspace.auth.application.usecase.LogoutUseCase;
import com.workspace.auth.application.usecase.RegisterUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    private static final String BEARER_PREFIX = "Bearer ";

    @PostMapping("/register")
    @Operation(summary = "Register new User")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = registerUserUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = loginUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader String accessToken) {
        logoutUseCase.logout(accessToken);
        return ResponseEntity.ok().build();
    }

}
