package com.example.talentbridge.shared.domain.exception;

/** A file could not be stored, read or deleted. */
public class StorageException extends RuntimeException {
    public StorageException(String message) {
        super(message);
    }
}
