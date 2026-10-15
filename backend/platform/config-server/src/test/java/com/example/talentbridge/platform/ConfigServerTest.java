package com.example.talentbridge.platform;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConfigServerTest {

    @Autowired TestRestTemplate http;
    @Autowired ObjectMapper json;

    @Test
    void servesSharedAndServiceSpecificSettings() throws Exception {
        JsonNode env = json.readTree(http.getForObject("/monolith/default", String.class));
        assertThat(env.get("name").asText()).isEqualTo("monolith");
        String sources = env.get("propertySources").toString();
        assertThat(sources).contains("config-repo/monolith.yml").contains("config-repo/application.yml");
        assertThat(sources).contains("jwt.jwk-set-uri").contains("eureka.client.service-url.defaultZone");
        assertThat(http.getForObject("/actuator/health", String.class)).contains("UP");
    }
}
