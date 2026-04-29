package com.workspace.auth.adapters.controllers.mapper;

import com.workspace.auth.adapters.controllers.dto.UserResponse;
import com.workspace.auth.usecases.register.RegisterUserOutput;
import org.springframework.stereotype.Component;

@Component
public class UserOutputMapper {

    public UserResponse toUserResponse(RegisterUserOutput output) {
        return new UserResponse(
                output.userId(),
                output.email(),
                output.role(),
                output.status(),
                output.createdAt()
        );
    }
}