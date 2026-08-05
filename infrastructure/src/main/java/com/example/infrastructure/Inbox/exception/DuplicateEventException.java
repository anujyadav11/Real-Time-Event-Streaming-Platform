package com.example.infrastructure.Inbox.exception;

import java.util.UUID;

public class DuplicateEventException
        extends RuntimeException {
    public DuplicateEventException(UUID eventId) {
        super("Duplicate event: " + eventId);
    }

}