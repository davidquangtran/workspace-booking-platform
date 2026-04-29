package com.workspace.auth.adapters.controllers.mapper;

import com.workspace.auth.adapters.controllers.dto.LoginResponse;
import com.workspace.auth.adapters.controllers.dto.RefreshTokenResponse;
import com.workspace.auth.adapters.controllers.dto.UserResponse;
import com.workspace.auth.usecases.login.LoginUserOutput;
import com.workspace.auth.usecases.refresh.RefreshAccessTokenOutput;
import com.workspace.auth.usecases.register.RegisterUserOutput;
import org.springframework.stereotype.Component;

@Component
public class UserOutputMapper {

    public UserResponse toUserResponse(RegisterUserOutput output) {
        return new UserResponse(
                output.userId(),
                output.email(),
                output.role(),
                output.status(),
                output.createdAt()
        );
    }

    public LoginResponse toLoginResponse(LoginUserOutput output) {
        return new LoginResponse(
                output.userId(),
                output.email(),
                output.role(),
                new LoginResponse.TokenInfo(output.accessToken(), output.accessTokenExpiresAt()),
                new LoginResponse.TokenInfo(output.refreshToken(), output.refreshTokenExpiresAt())
        );
    }

    public RefreshTokenResponse toRefreshTokenResponse(RefreshAccessTokenOutput output) {
        return new RefreshTokenResponse(
                new RefreshTokenResponse.TokenInfo(output.accessToken(), output.accessTokenExpiresAt()),
                new RefreshTokenResponse.TokenInfo(output.refreshToken(), output.refreshTokenExpiresAt())
        );
    }}