package com.workspace.auth.application.port;

public interface TokenHasher {
    String sha256(String plaintext);
}
