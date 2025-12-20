package com.example.talentbridge.config.network;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
@ConditionalOnProperty(name = { "gcs.project-id", "gcs.bucket-name" }, matchIfMissing = false)
public class GCSConfig {

    @Value("${gcs.project-id}")
    private String projectId;

    @Value("${gcs.bucket-name}")
    private String bucketName;

    @Value("${gcs.credentials-path:}")
    private String credentialsPath;

    @Bean
    public Storage storage() throws IOException {
        GoogleCredentials credentials;

        // Try to load from file first, fallback to Application Default Credentials (for
        // Cloud Run)
        if (credentialsPath != null && !credentialsPath.isEmpty()) {
            try {
                credentials = GoogleCredentials.fromStream(
                        new FileInputStream(credentialsPath));
            } catch (Exception e) {
                // Use Application Default Credentials (works on GCP)
                credentials = GoogleCredentials.getApplicationDefault();
            }
        } else {
            // Use Application Default Credentials (works on GCP)
            credentials = GoogleCredentials.getApplicationDefault();
        }

        return StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(credentials)
                .build()
                .getService();
    }

    @Bean
    public String gcsBucketName() {
        return bucketName;
    }

    @Bean
    public String gcsProjectId() {
        return projectId;
    }
}
