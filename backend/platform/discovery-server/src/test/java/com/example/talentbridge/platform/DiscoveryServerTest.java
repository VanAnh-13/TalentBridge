package com.example.talentbridge.platform;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DiscoveryServerTest {

    @Autowired TestRestTemplate http;

    @Test
    void servesTheRegistryAndHealth() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        ResponseEntity<String> apps = http.exchange("/eureka/apps", HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertThat(apps.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(apps.getBody()).contains("applications");
        assertThat(http.getForObject("/actuator/health", String.class)).contains("UP");
    }
}
