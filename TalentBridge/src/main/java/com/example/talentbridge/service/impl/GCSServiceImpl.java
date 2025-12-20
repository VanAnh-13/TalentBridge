package com.example.talentbridge.service.impl;

import com.example.talentbridge.advice.exception.S3UploadException;
import com.example.talentbridge.service.S3Service;
import com.google.cloud.storage.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "storage", name = "type", havingValue = "gcs")
public class GCSServiceImpl implements S3Service {

    private final Storage storage;
    private final String gcsBucketName;

    @Override
    public String uploadFile(MultipartFile file, String folder, String fileName, boolean getUrl) {
        try {
            if (file == null || file.isEmpty())
                throw new S3UploadException("Tệp logo không được rỗng hoặc null");

            String key = String.format("%s/%s", folder, fileName);

            BlobId blobId = BlobId.of(gcsBucketName, key);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();

            storage.create(blobInfo, file.getBytes());

            if (getUrl)
                return String.format("https://storage.googleapis.com/%s/%s", gcsBucketName, key);
            else
                return key;
        } catch (IOException e) {
            throw new S3UploadException("Lỗi khi đọc dữ liệu từ tệp");
        } catch (Exception e) {
            throw new S3UploadException("Lỗi khi upload file lên GCS: " + e.getMessage());
        }
    }

    @Override
    public String uploadFile(MultipartFile file, String key, boolean getUrl) {
        try {
            if (file == null || file.isEmpty())
                throw new S3UploadException("Tệp logo không được rỗng hoặc null");

            BlobId blobId = BlobId.of(gcsBucketName, key);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();

            storage.create(blobInfo, file.getBytes());

            if (getUrl)
                return String.format("https://storage.googleapis.com/%s/%s", gcsBucketName, key);
            else
                return key;
        } catch (IOException e) {
            throw new S3UploadException("Lỗi khi đọc dữ liệu từ tệp logo");
        } catch (Exception e) {
            throw new S3UploadException("Lỗi khi upload file lên GCS: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(cacheNames = "presign", key = "#key + ':' + #expireDuration.seconds")
    public String generatePresignedUrl(String key, Duration expireDuration) {
        // Return null for invalid keys to prevent API crashes
        if (key == null || key.isBlank()) {
            return null;
        }

        try {
            BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(gcsBucketName, key)).build();

            URL signedUrl = storage.signUrl(
                    blobInfo,
                    expireDuration.toMinutes(),
                    TimeUnit.MINUTES,
                    Storage.SignUrlOption.withV4Signature());

            return signedUrl.toString();
        } catch (Exception e) {
            // Fallback to public URL if signing fails (for Cloud Run without private key)
            System.err.println("Warning: Failed to sign URL, using public URL. Key: " + key + " - " + e.getMessage());
            return String.format("https://storage.googleapis.com/%s/%s", gcsBucketName, key);
        }
    }

    @Override
    public void deleteFileByUrl(String fileUrl) {
        try {
            if (fileUrl == null || fileUrl.isBlank()) {
                return;
            }

            String objectKey = extractObjectKeyFromUrl(fileUrl);

            BlobId blobId = BlobId.of(gcsBucketName, objectKey);
            storage.delete(blobId);

        } catch (Exception e) {
            throw new S3UploadException("Lỗi khi xóa file khỏi GCS: " + e.getMessage());
        }
    }

    @Override
    public void deleteFileByKey(String key) {
        try {
            if (key == null || key.isBlank()) {
                return;
            }

            BlobId blobId = BlobId.of(gcsBucketName, key);
            storage.delete(blobId);

        } catch (Exception e) {
            throw new S3UploadException("Lỗi khi xóa file khỏi GCS: " + e.getMessage());
        }
    }

    private String extractObjectKeyFromUrl(String url) {
        String base = String.format("https://storage.googleapis.com/%s/", gcsBucketName);
        if (!url.startsWith(base)) {
            throw new S3UploadException("URL không hợp lệ hoặc không thuộc bucket hiện tại");
        }
        return url.substring(base.length());
    }
}
