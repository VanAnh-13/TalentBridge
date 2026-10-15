package com.example.talentbridge.shared.presentation;

import com.example.talentbridge.shared.application.FileUpload;
import com.example.talentbridge.shared.domain.exception.StorageException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/** Converts the web layer's {@link MultipartFile} into the framework-free {@link FileUpload}. */
public final class FileUploads {

    private FileUploads() {
    }

    /** {@code null} in, {@code null} out. */
    public static FileUpload from(MultipartFile file) {
        if (file == null)
            return null;
        try {
            return new FileUpload(file.getOriginalFilename(), file.getContentType(), file.getBytes());
        } catch (IOException e) {
            throw new StorageException("Lỗi khi đọc dữ liệu từ tệp");
        }
    }
}
