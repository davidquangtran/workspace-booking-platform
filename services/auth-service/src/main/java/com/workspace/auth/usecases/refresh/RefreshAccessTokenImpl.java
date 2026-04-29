package com.workspace.auth.usecases.refresh;

import com.workspace.auth.entities.RefreshToken;
import com.workspace.auth.entities.User;
import com.workspace.auth.entities.exception.InvalidRefreshTokenException;
import com.workspace.auth.usecases.ports.RefreshTokenGateway;
import com.workspace.auth.usecases.ports.TokenGenerator;
import com.workspace.auth.usecases.ports.UserGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshAccessTokenImpl implements RefreshAccessToken {

    private static final Logger log = LoggerFactory.getLogger(RefreshAccessTokenImpl.class);

    private final RefreshTokenGateway refreshTokenGateway;
    private final UserGateway userGateway;
    private final TokenGenerator tokenGenerator;

    public RefreshAccessTokenImpl(RefreshTokenGateway refreshTokenGateway,
                                  UserGateway userGateway,
                                  TokenGenerator tokenGenerator) {
        this.refreshTokenGateway = refreshTokenGateway;
        this.userGateway = userGateway;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    @Transactional
    public RefreshAccessTokenOutput execute(RefreshAccessTokenInput input) {
        // 1. Lookup refresh token in DB
        RefreshToken existing = refreshTokenGateway.findByToken(input.refreshToken())
                .orElseThrow(() -> new InvalidRefreshTokenException("Token not found"));

        // 2. Detect token reuse (security check)
        if (existing.isRevoked()) {
            // Token đã bị revoke nhưng vẫn được dùng → có khả năng bị steal
            // Best practice: revoke ALL refresh tokens của user này → force re-login
            log.warn("Detected reuse of revoked refresh token for user {}. Revoking all tokens.",
                    existing.getUserId().value());
            refreshTokenGateway.revokeAllByUserId(existing.getUserId());
            throw new InvalidRefreshTokenException("Token has been revoked");
        }

        // 3. Check expiration
        if (existing.isExpired()) {
            throw new InvalidRefreshTokenException("Token has expired");
        }

        // 4. Lookup user (đảm bảo user vẫn ACTIVE)
        User user = userGateway.findById(existing.getUserId())
                .orElseThrow(() -> new InvalidRefreshTokenException("User no longer exists"));

        if (!user.canLogin()) {
            throw new InvalidRefreshTokenException("User cannot login");
        }

        // 5. Rotate: revoke token cũ, issue token mới
        refreshTokenGateway.revoke(existing);

        TokenGenerator.GeneratedToken accessToken = tokenGenerator.generateAccessToken(user);
        TokenGenerator.GeneratedToken newRefreshToken = tokenGenerator.generateRefreshToken();

        RefreshToken newRt = RefreshToken.issue(
                user.getId(),
                newRefreshToken.value(),
                newRefreshToken.expiresAt()
        );
        refreshTokenGateway.save(newRt);

        // 6. Build output
        return new RefreshAccessTokenOutput(
                accessToken.value(),
                accessToken.expiresAt(),
                newRefreshToken.value(),
                newRefreshToken.expiresAt()
        );
    }
}