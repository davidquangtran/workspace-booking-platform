package com.workspace.auth.adapters.controllers;

import com.workspace.auth.adapters.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "User", description = "Authenticated user endpoints")
@SecurityRequirement(name = "bearerAuth")   // Require JWT
public class MeController {

    @Operation(
            summary = "Get current authenticated user",
            description = "Returns information about the currently authenticated user (extracted from JWT)."
    )
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(@AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(Map.of(
                "userId", user.userId(),
                "email", user.email(),
                "role", user.role()
        ));
    }
}