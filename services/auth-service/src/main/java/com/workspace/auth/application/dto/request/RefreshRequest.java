package com.workspace.auth.application.dto.request;

public record RefreshRequest(
        String deviceInfo,
        String ipAddress
) {
}
