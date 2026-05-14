package com.workspace.auth.infrastructure.security;

import com.workspace.auth.application.port.JwtPort;
import com.workspace.auth.domain.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtService implements JwtPort {
    private final SecretKey secretKey;
    private final long accessTokenExpiry;
    private final long refreshTokenExpiry;


    public JwtService(
            @Value("${auth.jwt.secret}") String secret,
            @Value("${auth.jwt.access-token-expiry}") long accessTokenExpiry,
            @Value("${auth.jwt.refresh-token-expiry}") long refreshTokenExpiry
    ) {
        this.secretKey          = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiry  = accessTokenExpiry;
        this.refreshTokenExpiry = refreshTokenExpiry;
    }

    @Override
    public String generateAccessToken(User user) {
        return buildToken(user, accessTokenExpiry, "access");
    }

    @Override
    public String generateRefreshToken(User user) {
        return buildToken(user, refreshTokenExpiry, "refresh");
    }

    @Override
    public long getAccessTokenExpiresIn() {
        return accessTokenExpiry / 1000;
    }

    private String buildToken(User user, long expiry, String tokenType){
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expiry);

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("role",  user.getRole().name())
                .claim("type",  tokenType)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }
}
