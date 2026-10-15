package com.example.talentbridge.shared.application;

import java.time.Duration;

/** Output port: where uploaded files (logos, avatars, CVs) are kept. */
public interface FileStorage {

    /** Stores {@code file} as {@code folder/fileName}; returns its public URL when {@code getUrl}, else its key. */
    String uploadFile(FileUpload file, String folder, String fileName, boolean getUrl);

    /** Stores {@code file} under {@code key}; returns its public URL when {@code getUrl}, else the key. */
    String uploadFile(FileUpload file, String key, boolean getUrl);

    String generatePresignedUrl(String key, Duration expireDuration);

    /** The content stored under {@code key}. */
    byte[] read(String key);

    void deleteFileByUrl(String fileUrl);

    void deleteFileByKey(String key);
}
