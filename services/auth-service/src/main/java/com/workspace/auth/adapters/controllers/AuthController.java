package com.workspace.auth.adapters.controllers;

import com.workspace.auth.adapters.controllers.dto.RegisterRequest;
import com.workspace.auth.adapters.controllers.dto.UserResponse;
import com.workspace.auth.adapters.controllers.mapper.UserOutputMapper;
import com.workspace.auth.usecases.register.RegisterUser;
import com.workspace.auth.usecases.register.RegisterUserInput;
import com.workspace.auth.usecases.register.RegisterUserOutput;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterUser registerUser;
    private final UserOutputMapper outputMapper;

    public AuthController(RegisterUser registerUser, UserOutputMapper outputMapper) {
        this.registerUser = registerUser;
        this.outputMapper = outputMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterUserInput input = new RegisterUserInput(request.email(), request.password());
        RegisterUserOutput output = registerUser.execute(input);
        UserResponse response = outputMapper.toUserResponse(output);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}