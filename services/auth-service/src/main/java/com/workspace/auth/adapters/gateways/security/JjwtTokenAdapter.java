package com.workspace.auth.adapters.gateways.security;

import com.workspace.auth.entities.User;
import com.workspace.auth.entities.UserId;
import com.workspace.auth.frameworks.config.JwtProperties;
import com.workspace.auth.usecases.ports.TokenGenerator;
import com.workspace.auth.usecases.ports.TokenVerifier;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JjwtTokenAdapter implements TokenGenerator, TokenVerifier {

    private final JwtProperties props;
    private final SecretKey signingKey;

    public JjwtTokenAdapter(JwtProperties props) {
        this.props = props;
        this.signingKey = Keys.hmacShaKeyFor(props.secret().getBytes(StandardCharsets.UTF_8));
    }

    // ============== TokenGenerator ==============

    @Override
    public GeneratedToken generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(Duration.ofMinutes(props.accessTokenExpirationMinutes()));

        String jwt = Jwts.builder()
                .subject(user.getId().value().toString())
                .claim("email", user.getEmail().value())
                .claim("role", user.getRole().name())
                .issuer(props.issuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();

        return new GeneratedToken(jwt, expiresAt);
    }

    @Override
    public GeneratedToken generateRefreshToken() {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(Duration.ofDays(props.refreshTokenExpirationDays()));

        String token = UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();

        return new GeneratedToken(token, expiresAt);
    }

    // ============== TokenVerifier ==============

    @Override
    public VerifiedToken verify(String token) {
        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(signingKey)
                    .requireIssuer(props.issuer())
                    .build()
                    .parseSignedClaims(token);

            Claims claims = jws.getPayload();

            return new VerifiedToken(
                    UserId.of(claims.getSubject()),
                    claims.get("email", String.class),
                    claims.get("role", String.class)
            );
        } catch (ExpiredJwtException ex) {
            throw new TokenVerificationException("Token has expired", ex);
        } catch (SignatureException ex) {
            throw new TokenVerificationException("Token signature is invalid", ex);
        } catch (MalformedJwtException ex) {
            throw new TokenVerificationException("Token is malformed", ex);
        } catch (JwtException ex) {
            throw new TokenVerificationException("Token verification failed: " + ex.getMessage(), ex);
        } catch (IllegalArgumentException ex) {
            throw new TokenVerificationException("Token is empty or null", ex);
        }
    }
}