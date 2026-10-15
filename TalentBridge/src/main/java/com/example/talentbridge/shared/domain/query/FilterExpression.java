package com.example.talentbridge.shared.domain.query;

/**
 * Client supplied filter in the public API's filter language (e.g. {@code name~'*java*' and level:'SENIOR'}).
 * The domain only carries it; infrastructure adapters translate it into a store specific query.
 */
public record FilterExpression(String value) {

    public static final FilterExpression NONE = new FilterExpression(null);

    public static FilterExpression of(String value) {
        return value == null || value.isBlank() ? NONE : new FilterExpression(value);
    }

    public boolean isEmpty() {
        return value == null || value.isBlank();
    }
}
