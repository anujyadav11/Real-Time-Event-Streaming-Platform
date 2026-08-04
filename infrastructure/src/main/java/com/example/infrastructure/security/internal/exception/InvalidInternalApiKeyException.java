package com.example.infrastructure.security.internal.exception;

public class InvalidInternalApiKeyException extends RuntimeException {
    public InvalidInternalApiKeyException() {
        super("Invalid internal API key");
    }
}
