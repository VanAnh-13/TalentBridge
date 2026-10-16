package com.example.talentbridge.auth.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/** The rules a new password must satisfy. */
public final class PasswordPolicy {

    private static final int MIN_LENGTH = 8;
    private static final Pattern UPPERCASE = Pattern.compile("[A-Z]");
    private static final Pattern NUMBER = Pattern.compile("[0-9]");

    private PasswordPolicy() {
    }

    /** Human readable violations; empty when the password is acceptable. */
    public static List<String> violations(String password) {
        List<String> errors = new ArrayList<>();
        if (password == null || password.length() < MIN_LENGTH)
            errors.add("The password must have at least 8 characters");
        if (password == null || !UPPERCASE.matcher(password).find())
            errors.add("The password must contain at least one uppercase letter");
        if (password == null || !NUMBER.matcher(password).find())
            errors.add("The password must contain at least one digit");
        return errors;
    }

    public static boolean hasMinLength(String password) {
        return password != null && password.length() >= MIN_LENGTH;
    }

    public static boolean hasUppercase(String password) {
        return password != null && UPPERCASE.matcher(password).find();
    }

    public static boolean hasNumber(String password) {
        return password != null && NUMBER.matcher(password).find();
    }

    public static boolean isValid(String password) {
        return violations(password).isEmpty();
    }

    public static boolean confirms(String password, String confirmation) {
        return password != null && password.equals(confirmation);
    }
}
