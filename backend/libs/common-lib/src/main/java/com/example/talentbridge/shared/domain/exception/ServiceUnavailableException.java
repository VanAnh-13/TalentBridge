package com.example.talentbridge.shared.domain.exception;

/** A capability the request depends on is switched off or currently unreachable. */
public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) {
        super(message);
    }
}
