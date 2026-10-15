package com.example.talentbridge.shared.infrastructure.storage;

import com.example.talentbridge.shared.application.FileStorage;
import com.example.talentbridge.shared.application.FileUpload;
import com.example.talentbridge.shared.domain.exception.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.UUID;

/**
 * Local file system implementation of {@link FileStorage} for development.
 * Used unless storage.type=gcs.
 */
@Service
@Slf4j
@ConditionalOnProperty(
        prefix = "storage",
        name = "type",
        havingValue = "local",
        matchIfMissing = true
)
public class LocalFileStorage implements FileStorage {

    private static final String UPLOAD_DIR = "uploads";
    
    public LocalFileStorage() {
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
    public String uploadFile(FileUpload file, String folder, String fileName, boolean getUrl) {
        try {
            if (file == null || file.isEmpty()) {
                throw new StorageException("Tệp không được rỗng hoặc null");
            }

            // Create folder path
            Path folderPath = Paths.get(UPLOAD_DIR, folder);
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }

            // Save file
            Path filePath = folderPath.resolve(fileName);
            Files.write(filePath, file.content());

            String key = String.format("%s/%s", folder, fileName);
            log.info("Uploaded file locally: {}", key);

            if (getUrl) {
                // Return a local file URL
                return String.format("file:///%s/%s", UPLOAD_DIR, key);
            } else {
                return key;
            }
        } catch (IOException e) {
            log.error("Error uploading file locally: {}", e.getMessage());
            throw new StorageException("Lỗi khi upload file locally: " + e.getMessage());
        }
    }

    @Override
    public String uploadFile(FileUpload file, String key, boolean getUrl) {
        try {
            if (file == null || file.isEmpty()) {
                throw new StorageException("Tệp không được rỗng hoặc null");
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
            Files.write(filePath, file.content());

            log.info("Uploaded file locally: {}", key);

            if (getUrl) {
                return String.format("file:///%s/%s", UPLOAD_DIR, key);
            } else {
                return key;
            }
        } catch (IOException e) {
            log.error("Error uploading file locally: {}", e.getMessage());
            throw new StorageException("Lỗi khi upload file locally: " + e.getMessage());
        }
    }

    @Override
    public String generatePresignedUrl(String key, Duration expireDuration) {
        // For local files, just return the file path
        Path filePath = Paths.get(UPLOAD_DIR, key);
        if (Files.exists(filePath)) {
            return String.format("file:///%s/%s", UPLOAD_DIR, key);
        } else {
            log.warn("File not found for presigned URL: {}", key);
            return String.format("file:///%s/%s", UPLOAD_DIR, key);
        }
    }

    @Override
    public byte[] read(String key) {
        try {
            return Files.readAllBytes(Paths.get(UPLOAD_DIR, key));
        } catch (IOException e) {
            throw new StorageException("Không đọc được tệp: " + key);
        }
    }

    @Override
    public void deleteFileByUrl(String fileUrl) {
        try {
            if (fileUrl == null || fileUrl.isBlank()) {
                return;
            }

            // Extract key from local URL
            String key;
            if (fileUrl.startsWith("file:///")) {
                String path = fileUrl.substring("file:///".length());
                if (path.startsWith(UPLOAD_DIR + "/")) {
                    key = path.substring((UPLOAD_DIR + "/").length());
                } else {
                    key = path;
                }
            } else {
                key = fileUrl;
            }

            deleteFileByKey(key);
        } catch (Exception e) {
            log.error("Error deleting file by URL: {}", e.getMessage());
            throw new StorageException("Lỗi khi xóa file: " + e.getMessage());
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
            throw new StorageException("Lỗi khi xóa file: " + e.getMessage());
        }
    }
}
