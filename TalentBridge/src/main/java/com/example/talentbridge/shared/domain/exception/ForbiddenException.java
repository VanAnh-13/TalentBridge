package com.example.talentbridge.shared.domain.exception;

/** The caller is authenticated but not allowed to perform the operation (HTTP 403). */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
