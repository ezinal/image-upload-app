package com.gardrops.imageuploadservice.exception;

public class SessionLimitExceededException extends RuntimeException {
    public SessionLimitExceededException(String message) {
        super(message);
    }
} 