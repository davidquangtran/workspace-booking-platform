package com.workspace.auth.infrastructure.security;

import com.workspace.auth.application.port.TokenPort;
import com.workspace.auth.domain.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtService implements TokenPort {

    private static final int REFRESH_TOKEN_BYTES = 32;   // 256 bits entropy
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();

    private final SecretKey secretKey;
    private final long accessTokenExpiry;


    public JwtService(@Value("${auth.jwt.secret}") String secret, @Value("${auth.jwt.access-token-expiry}") long accessTokenExpiry) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiry = accessTokenExpiry;
    }

    @Override
    public String generateAccessToken(User user) {

        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenExpiry);

        return Jwts.builder().id(UUID.randomUUID().toString()).subject(user.getId().toString()).claim("email", user.getEmail()).claim("role", user.getRole().name()).claim("type", "access").issuedAt(now).expiration(expiration).signWith(secretKey).compact();
    }

    @Override
    public String generateRefreshToken() {
        byte[] bytes = new byte[REFRESH_TOKEN_BYTES];
        SECURE_RANDOM.nextBytes(bytes);
        return URL_ENCODER.encodeToString(bytes);
    }

    @Override
    public long getAccessTokenExpiresIn() {
        return accessTokenExpiry / 1000;
    }

    @Override
    public UUID extractUserId(String accessToken) {

        Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(accessToken).getPayload();

        return UUID.fromString(claims.getSubject());
    }
}

