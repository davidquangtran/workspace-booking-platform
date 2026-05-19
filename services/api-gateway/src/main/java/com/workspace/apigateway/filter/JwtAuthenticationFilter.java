package com.workspace.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String TOKEN_TYPE_ACCESS = "access";

    /**
     * Public paths — KHÔNG yêu cầu JWT.
     * Login/register là endpoint ĐỂ LẤY JWT, không thể yêu cầu JWT trước khi gọi.
     */
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/refresh",
            "/actuator"
    );

    private final SecretKey secretKey;

    public JwtAuthenticationFilter(@Value("${auth.jwt.secret}") String secret) {
        // Tạo SecretKey một lần ở constructor — tái sử dụng cho mọi request,
        // KHÔNG tạo lại trong filter() vì sẽ tốn CPU không cần thiết.
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // ── (1) Public path → skip JWT validation, forward thẳng
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        // ── (2) Extract Bearer token
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return unauthorized(exchange, "Missing Authorization header");
        }
        String token = authHeader.substring(BEARER_PREFIX.length());

        // ── (3) Parse & verify JWT
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            // Đến được đây = chữ ký đúng + chưa hết hạn (JJWT auto-check exp)

            // ── (4) Reject refresh token used as access token
            String tokenType = claims.get("type", String.class);
            if (!TOKEN_TYPE_ACCESS.equals(tokenType)) {
                return unauthorized(exchange, "Wrong token type: " + tokenType);
            }

            // ── (5) Inject user info vào downstream request
            //        Auth-service sẽ đọc các header này thay vì decode JWT
            String userId = claims.getSubject();
            String email = claims.get("email", String.class);
            String role = claims.get("role", String.class);

            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Email", email)
                    .header("X-User-Role", role)
                    .build();

            log.debug("JWT verified: userId={}, email={}, role={}", userId, email, role);

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (ExpiredJwtException e) {
            return unauthorized(exchange, "Token expired at " + e.getClaims().getExpiration());
        } catch (SignatureException e) {
            return unauthorized(exchange, "Invalid token signature");
        } catch (JwtException e) {
            return unauthorized(exchange, "Invalid token: " + e.getMessage());
        }
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String reason) {
        log.warn("401 Unauthorized — path: {}, reason: {}",
                exchange.getRequest().getURI().getPath(), reason);
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        // Chạy SAU RequestLoggingFilter (đang ở HIGHEST_PRECEDENCE)
        // nhưng TRƯỚC các filter routing.
        // +100 đủ chỗ chen vào giữa, không xung đột với filter Spring built-in.
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }
}