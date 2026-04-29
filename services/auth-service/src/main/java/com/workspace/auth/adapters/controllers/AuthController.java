package com.workspace.auth.adapters.controllers;

import com.workspace.auth.adapters.controllers.dto.LoginRequest;
import com.workspace.auth.adapters.controllers.dto.LoginResponse;
import com.workspace.auth.adapters.controllers.dto.LogoutRequest;
import com.workspace.auth.adapters.controllers.dto.RefreshTokenRequest;
import com.workspace.auth.adapters.controllers.dto.RefreshTokenResponse;
import com.workspace.auth.adapters.controllers.dto.RegisterRequest;
import com.workspace.auth.adapters.controllers.dto.UserResponse;
import com.workspace.auth.adapters.controllers.mapper.UserOutputMapper;
import com.workspace.auth.adapters.exceptions.GlobalExceptionHandler.ErrorResponse;
import com.workspace.auth.usecases.login.LoginUser;
import com.workspace.auth.usecases.login.LoginUserInput;
import com.workspace.auth.usecases.login.LoginUserOutput;
import com.workspace.auth.usecases.logout.LogoutUser;
import com.workspace.auth.usecases.logout.LogoutUserInput;
import com.workspace.auth.usecases.refresh.RefreshAccessToken;
import com.workspace.auth.usecases.refresh.RefreshAccessTokenInput;
import com.workspace.auth.usecases.refresh.RefreshAccessTokenOutput;
import com.workspace.auth.usecases.register.RegisterUser;
import com.workspace.auth.usecases.register.RegisterUserInput;
import com.workspace.auth.usecases.register.RegisterUserOutput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User registration, login, token refresh, and logout")
@SecurityRequirements   // Override global security: these endpoints don't need JWT
public class AuthController {

    private final RegisterUser registerUser;
    private final LoginUser loginUser;
    private final RefreshAccessToken refreshAccessToken;
    private final LogoutUser logoutUser;
    private final UserOutputMapper outputMapper;

    public AuthController(RegisterUser registerUser,
                          LoginUser loginUser,
                          RefreshAccessToken refreshAccessToken,
                          LogoutUser logoutUser,
                          UserOutputMapper outputMapper) {
        this.registerUser = registerUser;
        this.loginUser = loginUser;
        this.refreshAccessToken = refreshAccessToken;
        this.logoutUser = logoutUser;
        this.outputMapper = outputMapper;
    }

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with the given email and password. " +
                    "Returns the created user info (without password). Email must be unique."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed (invalid email, weak password)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Email already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterUserInput input = new RegisterUserInput(request.email(), request.password());
        RegisterUserOutput output = registerUser.execute(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(outputMapper.toUserResponse(output));
    }

    @Operation(
            summary = "Login with email and password",
            description = "Authenticates user and returns a pair of tokens: " +
                    "access token (short-lived, for API requests) and refresh token (long-lived, for obtaining new access tokens)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid email or password",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginUserInput input = new LoginUserInput(request.email(), request.password());
        LoginUserOutput output = loginUser.execute(input);
        return ResponseEntity.ok(outputMapper.toLoginResponse(output));
    }

    @Operation(
            summary = "Refresh access token",
            description = "Exchanges a valid refresh token for a new pair of (access token, refresh token). " +
                    "Implements rotation: the old refresh token is revoked. " +
                    "If a revoked token is used, all tokens for the user are revoked (theft detection)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tokens refreshed",
                    content = @Content(schema = @Schema(implementation = RefreshTokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid, expired, or revoked refresh token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshAccessTokenInput input = new RefreshAccessTokenInput(request.refreshToken());
        RefreshAccessTokenOutput output = refreshAccessToken.execute(input);
        return ResponseEntity.ok(outputMapper.toRefreshTokenResponse(output));
    }

    @Operation(
            summary = "Logout (revoke refresh token)",
            description = "Revokes the given refresh token. Idempotent: calling multiple times with the same token is safe. " +
                    "Note: the access token issued before logout remains valid until its natural expiration (~15 minutes)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Logged out (or token already revoked / not found)"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
        LogoutUserInput input = new LogoutUserInput(request.refreshToken());
        logoutUser.execute(input);
        return ResponseEntity.noContent().build();
    }
}