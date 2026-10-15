package com.example.talentbridge.config;

import com.example.talentbridge.shared.application.FileStorage;
import com.example.talentbridge.shared.infrastructure.storage.LocalFileStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * The production profile defines gcs.project-id and gcs.bucket-name as empty strings. That must not make the
 * application try to authenticate to Google Cloud (it used to crash on start-up wherever no Google credentials
 * exist, e.g. docker compose).
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {"gcs.project-id=", "gcs.bucket-name=", "gcs.credentials-path=./none.json"})
class BlankGcsSettingsTest {

    @Autowired ApplicationContext context;
    @Autowired FileStorage storage;

    @Test
    @DisplayName("blank gcs.* settings leave local storage active and create no Google client")
    void blankSettingsMeanLocalStorage() {
        assertInstanceOf(LocalFileStorage.class, storage);
        assertFalse(context.containsBean("storage"), "no Google Storage client without storage.type=gcs");
    }
}
