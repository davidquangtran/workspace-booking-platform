package com.workspace.auth.usecases.register;

import com.workspace.auth.entities.Email;
import com.workspace.auth.entities.HashedPassword;
import com.workspace.auth.entities.PasswordPolicy;
import com.workspace.auth.entities.User;
import com.workspace.auth.entities.exception.EmailAlreadyExistsException;
import com.workspace.auth.usecases.ports.PasswordHasher;
import com.workspace.auth.usecases.ports.UserGateway;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterUserImpl implements RegisterUser {

    private final UserGateway userGateway;
    private final PasswordHasher passwordHasher;
    private final PasswordPolicy passwordPolicy;

    public RegisterUserImpl(UserGateway userGateway, PasswordHasher passwordHasher) {
        this.userGateway = userGateway;
        this.passwordHasher = passwordHasher;
        this.passwordPolicy = new PasswordPolicy();
    }

    @Override
    @Transactional
    public RegisterUserOutput execute(RegisterUserInput input) {
        // 1. Validate password
        passwordPolicy.validate(input.rawPassword());

        // 2. Build email VO (validates format)
        Email email = new Email(input.email());

        // 3. Check uniqueness
        if (userGateway.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        // 4. Hash password
        HashedPassword hashed = passwordHasher.hash(input.rawPassword());

        // 5. Create domain entity
        User newUser = User.register(email, hashed);

        // 6. Persist
        User saved = userGateway.save(newUser);

        // 7. Return Output DTO (NOT entity)
        return new RegisterUserOutput(
                saved.getId().value(),
                saved.getEmail().value(),
                saved.getRole().name(),
                saved.getStatus().name(),
                saved.getCreatedAt()
        );
    }
}