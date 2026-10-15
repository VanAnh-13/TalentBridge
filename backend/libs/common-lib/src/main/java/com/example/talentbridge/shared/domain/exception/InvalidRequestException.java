package com.example.talentbridge.shared.domain.exception;

/** The request is semantically invalid, e.g. a malformed filter expression (HTTP 400). */
public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
