package com.example.talentbridge.shared.infrastructure.storage;

import com.example.talentbridge.shared.domain.exception.StorageException;
import com.example.talentbridge.shared.presentation.FileUploads;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LocalFileStorage Unit Tests")
class LocalFileStorageTest {

    private LocalFileStorage s3LocalService;
    private static final String UPLOAD_DIR = "uploads";

    @BeforeEach
    void setUp() {
        s3LocalService = new LocalFileStorage();
    }

    @AfterEach
    void cleanUp() throws IOException {
        // Clean up test files after each test
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (Files.exists(uploadPath)) {
            Files.walk(uploadPath)
                    .sorted((p1, p2) -> -p1.compareTo(p2)) // Delete files before directories
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            // Ignore cleanup errors
                        }
                    });
        }
    }

    @Test
    @DisplayName("Should upload file successfully with folder and filename")
    void testUploadFileWithFolderAndFilename() {
        // Arrange
        String content = "Test file content";
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                content.getBytes()
        );
        String folder = "avatars";
        String fileName = "user123.txt";

        // Act
        String result = s3LocalService.uploadFile(FileUploads.from(file), folder, fileName, false);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals("avatars/user123.txt", result, "Should return correct key");
        
        // Verify file was actually created
        Path filePath = Paths.get(UPLOAD_DIR, folder, fileName);
        assertTrue(Files.exists(filePath), "File should exist on disk");
    }

    @Test
    @DisplayName("Should upload file and return URL when getUrl is true")
    void testUploadFileWithUrl() {
        // Arrange
        String content = "Test content for URL";
        MultipartFile file = new MockMultipartFile(
                "file",
                "test-url.txt",
                "text/plain",
                content.getBytes()
        );
        String folder = "documents";
        String fileName = "doc123.txt";

        // Act
        String result = s3LocalService.uploadFile(FileUploads.from(file), folder, fileName, true);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertTrue(result.startsWith("file:///"), "Should return file URL");
        assertTrue(result.contains("documents/doc123.txt"), "URL should contain the file path");
    }

    @Test
    @DisplayName("Should upload file with key path")
    void testUploadFileWithKey() {
        // Arrange
        String content = "Key-based upload test";
        MultipartFile file = new MockMultipartFile(
                "file",
                "original.txt",
                "text/plain",
                content.getBytes()
        );
        String key = "resumes/2024/resume123.pdf";

        // Act
        String result = s3LocalService.uploadFile(FileUploads.from(file), key, false);

        // Assert
        assertEquals(key, result, "Should return the same key");
        
        // Verify file was created at correct path
        Path filePath = Paths.get(UPLOAD_DIR, key);
        assertTrue(Files.exists(filePath), "File should exist at the specified key path");
    }

    @Test
    @DisplayName("Should throw exception when file is null")
    void testUploadNullFile() {
        // Arrange
        MultipartFile file = null;
        String folder = "test";
        String fileName = "test.txt";

        // Act & Assert
        assertThrows(StorageException.class, () -> {
            s3LocalService.uploadFile(FileUploads.from(file), folder, fileName, false);
        }, "Should throw StorageException for null file");
    }

    @Test
    @DisplayName("Should throw exception when file is empty")
    void testUploadEmptyFile() {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "empty.txt",
                "text/plain",
                new byte[0]
        );
        String folder = "test";
        String fileName = "empty.txt";

        // Act & Assert
        assertThrows(StorageException.class, () -> {
            s3LocalService.uploadFile(FileUploads.from(file), folder, fileName, false);
        }, "Should throw StorageException for empty file");
    }

    @Test
    @DisplayName("Should generate presigned URL for existing file")
    void testGeneratePresignedUrl() {
        // Arrange
        String content = "File for presigned URL";
        MultipartFile file = new MockMultipartFile(
                "file",
                "presign-test.txt",
                "text/plain",
                content.getBytes()
        );
        String key = "images/presign-test.txt";
        
        // Upload file first
        s3LocalService.uploadFile(FileUploads.from(file), key, false);

        // Act
        String presignedUrl = s3LocalService.generatePresignedUrl(key, Duration.ofHours(1));

        // Assert
        assertNotNull(presignedUrl, "Presigned URL should not be null");
        assertTrue(presignedUrl.startsWith("file:///"), "Should return file URL");
        assertTrue(presignedUrl.contains(key), "URL should contain the key");
    }

    @Test
    @DisplayName("Should generate presigned URL even for non-existent file")
    void testGeneratePresignedUrlNonExistentFile() {
        // Arrange
        String key = "non-existent/file.txt";

        // Act
        String presignedUrl = s3LocalService.generatePresignedUrl(key, Duration.ofHours(1));

        // Assert
        assertNotNull(presignedUrl, "Presigned URL should still be generated");
        assertTrue(presignedUrl.contains(key), "URL should contain the key");
    }

    @Test
    @DisplayName("Should delete file by key successfully")
    void testDeleteFileByKey() {
        // Arrange
        String content = "File to be deleted";
        MultipartFile file = new MockMultipartFile(
                "file",
                "delete-test.txt",
                "text/plain",
                content.getBytes()
        );
        String key = "temp/delete-test.txt";
        
        // Upload file first
        s3LocalService.uploadFile(FileUploads.from(file), key, false);
        Path filePath = Paths.get(UPLOAD_DIR, key);
        assertTrue(Files.exists(filePath), "File should exist before deletion");

        // Act
        s3LocalService.deleteFileByKey(key);

        // Assert
        assertFalse(Files.exists(filePath), "File should be deleted");
    }

    @Test
    @DisplayName("Should delete file by URL successfully")
    void testDeleteFileByUrl() {
        // Arrange
        String content = "File to be deleted by URL";
        MultipartFile file = new MockMultipartFile(
                "file",
                "delete-url-test.txt",
                "text/plain",
                content.getBytes()
        );
        String key = "temp/delete-url-test.txt";
        
        // Upload file and get URL
        String fileUrl = s3LocalService.uploadFile(FileUploads.from(file), key, true);
        Path filePath = Paths.get(UPLOAD_DIR, key);
        assertTrue(Files.exists(filePath), "File should exist before deletion");

        // Act
        s3LocalService.deleteFileByUrl(fileUrl);

        // Assert
        assertFalse(Files.exists(filePath), "File should be deleted");
    }

    @Test
    @DisplayName("Should handle deletion of non-existent file gracefully")
    void testDeleteNonExistentFile() {
        // Arrange
        String key = "non-existent/file.txt";

        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> {
            s3LocalService.deleteFileByKey(key);
        }, "Should handle non-existent file deletion gracefully");
    }

    @Test
    @DisplayName("Should handle null or blank key/URL in delete operations")
    void testDeleteWithNullOrBlankKey() {
        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> {
            s3LocalService.deleteFileByKey(null);
            s3LocalService.deleteFileByKey("");
            s3LocalService.deleteFileByKey("   ");
            s3LocalService.deleteFileByUrl(null);
            s3LocalService.deleteFileByUrl("");
            s3LocalService.deleteFileByUrl("   ");
        }, "Should handle null or blank keys gracefully");
    }

    @Test
    @DisplayName("Should create nested folders automatically")
    void testCreateNestedFolders() {
        // Arrange
        String content = "Nested folder test";
        MultipartFile file = new MockMultipartFile(
                "file",
                "nested.txt",
                "text/plain",
                content.getBytes()
        );
        String key = "level1/level2/level3/nested.txt";

        // Act
        String result = s3LocalService.uploadFile(FileUploads.from(file), key, false);

        // Assert
        assertEquals(key, result, "Should return the correct key");
        
        // Verify nested folders and file were created
        Path filePath = Paths.get(UPLOAD_DIR, key);
        assertTrue(Files.exists(filePath), "File should exist in nested folders");
        assertTrue(Files.isRegularFile(filePath), "Should be a regular file");
    }

    @Test
    @DisplayName("Should handle files with special characters in names")
    void testUploadFileWithSpecialCharacters() {
        // Arrange
        String content = "Special chars test";
        MultipartFile file = new MockMultipartFile(
                "file",
                "test file with spaces.txt",
                "text/plain",
                content.getBytes()
        );
        String folder = "special-chars";
        String fileName = "file_with_underscores-and-dashes.txt";

        // Act
        String result = s3LocalService.uploadFile(FileUploads.from(file), folder, fileName, false);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertTrue(result.contains(fileName), "Result should contain the file name");
        
        // Verify file was created
        Path filePath = Paths.get(UPLOAD_DIR, folder, fileName);
        assertTrue(Files.exists(filePath), "File should exist");
    }

    @Test
    @DisplayName("Should preserve file content after upload")
    void testFileContentPreservation() throws IOException {
        // Arrange
        String originalContent = "This is the original file content that should be preserved!";
        MultipartFile file = new MockMultipartFile(
                "file",
                "content-test.txt",
                "text/plain",
                originalContent.getBytes()
        );
        String key = "content/test.txt";

        // Act
        s3LocalService.uploadFile(FileUploads.from(file), key, false);

        // Assert
        Path filePath = Paths.get(UPLOAD_DIR, key);
        String savedContent = Files.readString(filePath);
        assertEquals(originalContent, savedContent, "File content should be preserved");
    }
}
