package com.example.talentbridge.shared.application;

/** A file handed to {@link FileStorage}: framework-free (no servlet / multipart types). */
public record FileUpload(String originalFilename, String contentType, byte[] content) {

    public boolean isEmpty() {
        return content == null || content.length == 0;
    }
}
