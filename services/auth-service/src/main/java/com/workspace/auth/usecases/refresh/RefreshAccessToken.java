package com.workspace.auth.usecases.refresh;

public interface RefreshAccessToken {
    RefreshAccessTokenOutput execute(RefreshAccessTokenInput input);
}