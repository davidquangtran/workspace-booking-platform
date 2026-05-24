package com.workspace.auth.presentation.controller;

import com.workspace.auth.application.dto.response.UserResponse;
import com.workspace.auth.application.usecase.UserUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "Hi")
public class UserController {

    private final UserUseCase userUseCase;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@RequestHeader String accessToken) {
        UserResponse response = userUseCase.getInfo(accessToken);
        return ResponseEntity.ok(response);
    }
}
