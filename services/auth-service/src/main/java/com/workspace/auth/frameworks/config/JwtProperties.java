package com.workspace.auth.frameworks.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "auth.jwt")
@Validated
public record JwtProperties(

        @NotBlank(message = "JWT secret must not be blank")
        String secret,

        @NotBlank(message = "JWT issuer must not be blank")
        String issuer,

        @Positive(message = "Access token expiration must be positive")
        long accessTokenExpirationMinutes,

        @Positive(message = "Refresh token expiration must be positive")
        long refreshTokenExpirationDays
) {
}