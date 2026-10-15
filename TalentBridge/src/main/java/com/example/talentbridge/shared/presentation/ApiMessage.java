package com.example.talentbridge.shared.presentation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to standardize API response messages.
 * <p>
 * When attached to a controller method, the system will automatically
 * insert the `message` value into the JSON response according to the standard format:
 * <p>
 * {
 * "message": "...",
 * "errorCode": "...",
 * "data": { ... }
 * }
 * <p>
 * This annotation will be processed via @ControllerAdvice or AOP depending on your implementation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ApiMessage {
    String value();
}


