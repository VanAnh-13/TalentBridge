package com.example.talentbridge.shared.infrastructure.storage;

import com.example.talentbridge.shared.application.FileStorage;
import com.example.talentbridge.shared.application.FileUpload;
import com.example.talentbridge.shared.domain.exception.StorageException;
import com.google.cloud.storage.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "storage",
        name = "type",
        havingValue = "gcs"
)
public class GcsFileStorage implements FileStorage {

    private final Storage storage;
    private final String gcsBucketName;

    @Override
    public String uploadFile(FileUpload file, String folder, String fileName, boolean getUrl) {
        try {
            if (file == null || file.isEmpty())
                throw new StorageException("Tệp logo không được rỗng hoặc null");

            String key = String.format("%s/%s", folder, fileName);

            BlobId blobId = BlobId.of(gcsBucketName, key);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.contentType())
                    .build();

            storage.create(blobInfo, file.content());

            if (getUrl)
                return String.format("https://storage.googleapis.com/%s/%s", gcsBucketName, key);
            else return key;
        } catch (Exception e) {
            throw new StorageException("Lỗi khi upload file lên GCS: " + e.getMessage());
        }
    }

    @Override
    public String uploadFile(FileUpload file, String key, boolean getUrl) {
        try {
            if (file == null || file.isEmpty())
                throw new StorageException("Tệp logo không được rỗng hoặc null");

            BlobId blobId = BlobId.of(gcsBucketName, key);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.contentType())
                    .build();

            storage.create(blobInfo, file.content());

            if (getUrl)
                return String.format("https://storage.googleapis.com/%s/%s", gcsBucketName, key);
            else return key;
        } catch (Exception e) {
            throw new StorageException("Lỗi khi upload file lên GCS: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(
            cacheNames = "presign",
            key = "#key + ':' + #expireDuration.seconds"
    )
    public String generatePresignedUrl(String key, Duration expireDuration) {
        try {
            BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(gcsBucketName, key)).build();
            
            URL signedUrl = storage.signUrl(
                    blobInfo,
                    expireDuration.toMinutes(),
                    TimeUnit.MINUTES,
                    Storage.SignUrlOption.withV4Signature()
            );
            
            return signedUrl.toString();
        } catch (Exception e) {
            throw new StorageException("Lỗi khi tạo presigned URL: " + e.getMessage());
        }
    }

    @Override
    public byte[] read(String key) {
        try {
            byte[] content = storage.readAllBytes(BlobId.of(gcsBucketName, key));
            if (content == null) throw new StorageException("Không đọc được tệp: " + key);
            return content;
        } catch (StorageException e) {
            throw e;
        } catch (Exception e) {
            throw new StorageException("Lỗi khi đọc file từ GCS: " + e.getMessage());
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
            throw new StorageException("Lỗi khi xóa file khỏi GCS: " + e.getMessage());
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
            throw new StorageException("Lỗi khi xóa file khỏi GCS: " + e.getMessage());
        }
    }

    private String extractObjectKeyFromUrl(String url) {
        String base = String.format("https://storage.googleapis.com/%s/", gcsBucketName);
        if (!url.startsWith(base)) {
            throw new StorageException("URL không hợp lệ hoặc không thuộc bucket hiện tại");
        }
        return url.substring(base.length());
    }
}
