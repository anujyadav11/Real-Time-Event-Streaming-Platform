package com.example.eventstream.common.exception;

public class PlatformException extends RuntimeException {
    public PlatformException(String message) {
        super(message);
    }

    public PlatformException(String message, Throwable cause) {
        super(message, cause);
    }
}
