package com.example.talentbridge.advice.exception;

/** A request is well-formed but violates a business rule (answered with HTTP 409). */
public class BusinessRuleViolationException extends RuntimeException {
    public BusinessRuleViolationException(String message) {
        super(message);
    }
}
