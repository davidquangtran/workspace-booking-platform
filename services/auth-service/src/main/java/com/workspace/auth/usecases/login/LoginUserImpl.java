package com.workspace.auth.usecases.login;

import com.workspace.auth.entities.Email;
import com.workspace.auth.entities.RefreshToken;
import com.workspace.auth.entities.User;
import com.workspace.auth.entities.exception.InvalidCredentialsException;
import com.workspace.auth.usecases.ports.PasswordHasher;
import com.workspace.auth.usecases.ports.RefreshTokenGateway;
import com.workspace.auth.usecases.ports.TokenGenerator;
import com.workspace.auth.usecases.ports.UserGateway;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginUserImpl implements LoginUser {

    private final UserGateway userGateway;
    private final PasswordHasher passwordHasher;
    private final TokenGenerator tokenGenerator;
    private final RefreshTokenGateway refreshTokenGateway;

    public LoginUserImpl(UserGateway userGateway,
                         PasswordHasher passwordHasher,
                         TokenGenerator tokenGenerator,
                         RefreshTokenGateway refreshTokenGateway) {
        this.userGateway = userGateway;
        this.passwordHasher = passwordHasher;
        this.tokenGenerator = tokenGenerator;
        this.refreshTokenGateway = refreshTokenGateway;
    }

    @Override
    @Transactional
    public LoginUserOutput execute(LoginUserInput input) {
        // 1. Validate email format (ném InvalidCredentials nếu sai format)
        Email email;
        try {
            email = new Email(input.email());
        } catch (Exception ex) {
            throw new InvalidCredentialsException();
        }

        // 2. Find user
        User user = userGateway.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        // 3. Check user can login (status ACTIVE)
        if (!user.canLogin()) {
            throw new InvalidCredentialsException();
        }

        // 4. Verify password
        if (!passwordHasher.matches(input.rawPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        // 5. Generate tokens
        TokenGenerator.GeneratedToken accessToken = tokenGenerator.generateAccessToken(user);
        TokenGenerator.GeneratedToken refreshToken = tokenGenerator.generateRefreshToken();

        // 6. Persist refresh token
        RefreshToken rt = RefreshToken.issue(
                user.getId(),
                refreshToken.value(),
                refreshToken.expiresAt()
        );
        refreshTokenGateway.save(rt);

        // 7. Build output
        return new LoginUserOutput(
                user.getId().value(),
                user.getEmail().value(),
                user.getRole().name(),
                accessToken.value(),
                accessToken.expiresAt(),
                refreshToken.value(),
                refreshToken.expiresAt()
        );
    }
}