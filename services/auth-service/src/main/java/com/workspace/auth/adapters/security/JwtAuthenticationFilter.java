package com.workspace.auth.adapters.security;

import com.workspace.auth.usecases.ports.TokenVerifier;
import com.workspace.auth.usecases.ports.TokenVerifier.TokenVerificationException;
import com.workspace.auth.usecases.ports.TokenVerifier.VerifiedToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final TokenVerifier tokenVerifier;

    public JwtAuthenticationFilter(TokenVerifier tokenVerifier) {
        this.tokenVerifier = tokenVerifier;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = extractToken(request);

        if (token == null) {
            // No token → just pass through. AuthorizationFilter will reject if endpoint is protected.
            filterChain.doFilter(request, response);
            return;
        }

        try {
            VerifiedToken verified = tokenVerifier.verify(token);

            AuthenticatedUser principal = new AuthenticatedUser(
                    verified.userId().value(),
                    verified.email(),
                    verified.role()
            );

            // Spring's "Authentication" object — represents authenticated user
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    principal,
                    null, // credentials = null because already verified by JWT
                    List.of(new SimpleGrantedAuthority("ROLE_" + verified.role()))
            );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Put into SecurityContext — downstream code can access via SecurityContextHolder
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (TokenVerificationException ex) {
            log.debug("JWT verification failed: {}", ex.getMessage());
            // Don't set authentication. AuthorizationFilter will reject if needed.
            // Don't throw — let the request continue, let AuthorizationFilter return 403.
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return header.substring(BEARER_PREFIX.length()).trim();
    }
}