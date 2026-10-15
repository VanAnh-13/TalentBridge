package com.example.talentbridge.shared.domain.exception;

/** A token is malformed, expired or no longer valid (HTTP 401). A null message selects the default text. */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException() {
        super((String) null);
    }

    public InvalidTokenException(String message) {
        super(message);
    }
}
