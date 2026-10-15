package com.example.talentbridge.platform;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** The gateway (a real server on a random port) in front of a stand-in monolith that echoes what it received. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiGatewayTest {

    static final HttpServer monolith = start("monolith");
    static final HttpServer notifications = start("notification-service");

    static HttpServer start(String name) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            server.createContext("/", exchange -> {
                String echo = (name.equals("monolith") ? "" : name + ": ") + exchange.getRequestMethod() + " "
                        + exchange.getRequestURI() + " id=" + exchange.getRequestHeaders().getFirst("X-Request-Id");
                byte[] body = echo.getBytes(StandardCharsets.UTF_8);
                String origin = exchange.getRequestHeaders().getFirst("Origin");
                if (origin != null) {   // the monolith answers CORS as well
                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", origin);
                    exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
                }
                exchange.sendResponseHeaders(200, body.length);
                exchange.getResponseBody().write(body);
                exchange.close();
            });
            server.start();
            return server;
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @AfterAll
    static void stop() {
        monolith.stop(0);
        notifications.stop(0);
    }

    @DynamicPropertySource
    static void routes(DynamicPropertyRegistry registry) {
        registry.add("MONOLITH_URI", () -> "http://127.0.0.1:" + monolith.getAddress().getPort());
        registry.add("NOTIFICATION_URI", () -> "http://127.0.0.1:" + notifications.getAddress().getPort());
        registry.add("ALLOWED_ORIGINS", () -> "http://localhost:3000");
    }

    @LocalServerPort int port;
    WebTestClient web;

    @org.junit.jupiter.api.BeforeEach
    void client() {
        web = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    }

    @Test @DisplayName("requests reach the monolith unchanged, with a request id that is also returned")
    void forwards() {
        web.get().uri("/jobs?page=2").exchange()
                .expectStatus().isOk()
                .expectHeader().exists("X-Request-Id")
                .expectBody(String.class).value(body -> assertThat(body).startsWith("GET /jobs?page=2 id="));

        web.post().uri("/auth/login").header("X-Request-Id", "trace-12345678").exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("X-Request-Id", "trace-12345678")
                .expectBody(String.class).isEqualTo("POST /auth/login id=trace-12345678");

        // a header that could smuggle anything into logs is replaced
        web.get().uri("/jobs").header("X-Request-Id", "bad value; injected=1").exchange()
                .expectHeader().value("X-Request-Id", id -> assertThat(id).hasSize(36));
    }

    @Test @DisplayName("extracted paths go to their service, everything else to the monolith")
    void routes() {
        web.get().uri("/notifications?unread=true").exchange().expectStatus().isOk()
                .expectBody(String.class).value(body -> assertThat(body).startsWith("notification-service: GET /notifications?unread=true"));
        web.patch().uri("/notifications/5/read").exchange().expectStatus().isOk()
                .expectBody(String.class).value(body -> assertThat(body).startsWith("notification-service: PATCH /notifications/5/read"));
        web.get().uri("/notificationsfoo").exchange().expectStatus().isOk()
                .expectBody(String.class).value(body -> assertThat(body).startsWith("GET /notificationsfoo"));
        web.get().uri("/emails/job-recommendation/x").exchange()
                .expectBody(String.class).value(body -> assertThat(body).startsWith("GET /emails"));
    }

    @Test @DisplayName("the gateway answers CORS preflights and the browser sees each CORS header once")
    void cors() {
        web.options().uri("/resumes")
                .header(HttpHeaders.ORIGIN, "http://localhost:3000")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "PATCH")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:3000")
                .expectHeader().value(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, m -> assertThat(m).contains("PATCH"));

        web.get().uri("/jobs").header(HttpHeaders.ORIGIN, "http://localhost:3000").exchange()
                .expectStatus().isOk()
                .expectHeader().values(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, v -> assertThat(v).isEqualTo(List.of("http://localhost:3000")));

        web.get().uri("/jobs").header(HttpHeaders.ORIGIN, "http://evil.example").exchange()
                .expectStatus().isForbidden();
    }

    @Test @DisplayName("the services' actuator endpoints are not reachable through the gateway")
    void hidesInternals() {
        web.get().uri("/actuator/env").exchange().expectStatus().isNotFound();
        web.get().uri("/actuator/health").exchange().expectStatus().isOk();   // the gateway's own
    }
}
