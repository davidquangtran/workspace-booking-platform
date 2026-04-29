package com.workspace.auth.usecases.logout;

import com.workspace.auth.entities.RefreshToken;
import com.workspace.auth.usecases.ports.RefreshTokenGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class LogoutUserImpl implements LogoutUser {

    private static final Logger log = LoggerFactory.getLogger(LogoutUserImpl.class);

    private final RefreshTokenGateway refreshTokenGateway;

    public LogoutUserImpl(RefreshTokenGateway refreshTokenGateway) {
        this.refreshTokenGateway = refreshTokenGateway;
    }

    @Override
    @Transactional
    public void execute(LogoutUserInput input) {
        Optional<RefreshToken> token = refreshTokenGateway.findByToken(input.refreshToken());

        if (token.isEmpty()) {
            // Token không tồn tại — vẫn return success (không leak info)
            log.debug("Logout called with non-existent refresh token");
            return;
        }

        RefreshToken rt = token.get();
        if (rt.isRevoked()) {
            // Token đã revoke rồi — idempotent, return success
            return;
        }

        refreshTokenGateway.revoke(rt);
    }
}