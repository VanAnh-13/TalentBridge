package com.example.talentbridge.service.impl;

import com.example.talentbridge.advice.exception.S3UploadException;
import com.example.talentbridge.service.S3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.UUID;

/**
 * Local file system implementation of S3Service for development
 * This is used when AWS is not configured
 */
@Service
@Slf4j
@ConditionalOnProperty(prefix = "storage", name = "type", havingValue = "local", matchIfMissing = true)
public class S3LocalServiceImpl implements S3Service {

    private static final String UPLOAD_DIR = "uploads";

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public S3LocalServiceImpl() {
        // Create uploads directory if it doesn't exist
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Created local upload directory: {}", uploadPath.toAbsolutePath());
            }
        } catch (IOException e) {
            log.warn("Failed to create upload directory: {}", e.getMessage());
        }
    }

    @Override
    public String uploadFile(MultipartFile file, String folder, String fileName, boolean getUrl) {
        try {
            if (file == null || file.isEmpty()) {
                throw new S3UploadException("Tệp không được rỗng hoặc null");
            }

            // Create folder path
            Path folderPath = Paths.get(UPLOAD_DIR, folder);
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }

            // Save file
            Path filePath = folderPath.resolve(fileName);
            file.transferTo(filePath.toFile());

            String key = String.format("%s/%s", folder, fileName);
            log.info("Uploaded file locally: {}", key);

            if (getUrl) {
                // Return a HTTP URL
                return String.format("%s/%s/%s", baseUrl, UPLOAD_DIR, key);
            } else {
                return key;
            }
        } catch (IOException e) {
            log.error("Error uploading file locally: {}", e.getMessage());
            throw new S3UploadException("Lỗi khi upload file locally: " + e.getMessage());
        }
    }

    @Override
    public String uploadFile(MultipartFile file, String key, boolean getUrl) {
        try {
            if (file == null || file.isEmpty()) {
                throw new S3UploadException("Tệp không được rỗng hoặc null");
            }

            // Parse folder and filename from key
            Path keyPath = Paths.get(key);
            Path folderPath = Paths.get(UPLOAD_DIR);

            if (keyPath.getParent() != null) {
                folderPath = Paths.get(UPLOAD_DIR, keyPath.getParent().toString());
                if (!Files.exists(folderPath)) {
                    Files.createDirectories(folderPath);
                }
            }

            // Save file
            Path filePath = Paths.get(UPLOAD_DIR, key);
            file.transferTo(filePath.toFile());

            log.info("Uploaded file locally: {}", key);

            if (getUrl) {
                return String.format("%s/%s/%s", baseUrl, UPLOAD_DIR, key);
            } else {
                return key;
            }
        } catch (IOException e) {
            log.error("Error uploading file locally: {}", e.getMessage());
            throw new S3UploadException("Lỗi khi upload file locally: " + e.getMessage());
        }
    }

    @Override
    public String generatePresignedUrl(String key, Duration expireDuration) {
        // For local files, return the HTTP URL
        // We check existence but in a static resource context we just return the link
        Path filePath = Paths.get(UPLOAD_DIR, key);
        if (Files.exists(filePath)) {
            return String.format("%s/%s/%s", baseUrl, UPLOAD_DIR, key);
        } else {
            log.warn("File not found for presigned URL: {}", key);
            return String.format("%s/%s/%s", baseUrl, UPLOAD_DIR, key);
        }
    }

    @Override
    public void deleteFileByUrl(String fileUrl) {
        try {
            if (fileUrl == null || fileUrl.isBlank()) {
                return;
            }

            // Extract key from URL
            // Format: http://localhost:8080/uploads/{key}
            // or old format file:///...
            String key = null;

            if (fileUrl.startsWith("file:///")) {
                String path = fileUrl.substring("file:///".length());
                if (path.startsWith(UPLOAD_DIR + "/")) {
                    key = path.substring((UPLOAD_DIR + "/").length());
                } else {
                    key = path;
                }
            } else if (fileUrl.contains("/" + UPLOAD_DIR + "/")) {
                int index = fileUrl.indexOf("/" + UPLOAD_DIR + "/");
                key = fileUrl.substring(index + ("/" + UPLOAD_DIR + "/").length());
            }

            if (key != null) {
                deleteFileByKey(key);
            }
        } catch (Exception e) {
            log.error("Error deleting file by URL: {}", e.getMessage());
            throw new S3UploadException("Lỗi khi xóa file: " + e.getMessage());
        }
    }

    @Override
    public void deleteFileByKey(String key) {
        try {
            if (key == null || key.isBlank()) {
                return;
            }

            Path filePath = Paths.get(UPLOAD_DIR, key);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Deleted local file: {}", key);
            } else {
                log.warn("File not found for deletion: {}", key);
            }
        } catch (IOException e) {
            log.error("Error deleting file: {}", e.getMessage());
            throw new S3UploadException("Lỗi khi xóa file: " + e.getMessage());
        }
    }
}
