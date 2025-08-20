package com.familyFirstSoftware.SecureDocAIBackend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Minimal integration test to ensure the application context loads successfully.
 *
 * --- Best Practices ---
 * 1.  @SpringBootTest: This annotation loads the entire Spring application context, making it
 *     a true integration test. Use `webEnvironment = WebEnvironment.RANDOM_PORT` to start the
 *     server on a random port, avoiding conflicts during builds.
 * 2.  TestRestTemplate: A convenient client for making HTTP requests to the running application.
 *     It's great for simple integration tests.
 * 3.  Keep it Minimal: The goal of a "smoke test" is just to see if the application starts
 *     without errors. Test one or two simple, unauthenticated endpoints like actuator/health.
 *     Avoid complex business logic.
 * 4.  When to use Testcontainers: While H2 is great for repository tests, full integration
 *     tests benefit from running against a real database. Use Testcontainers to spin up a
 *     PostgreSQL Docker container for your @SpringBootTest runs. This provides maximum
 *     fidelity and catches environment-specific bugs. You would typically create a separate
 *     test profile or a base test class to enable Testcontainers.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Application Smoke Test")
class SecureDocAiBackendApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Application context loads and actuator health endpoint is available")
    void contextLoads_and_healthEndpointReturnsUp() {
        // Arrange
        String healthEndpointUrl = "http://localhost:" + port + "/actuator/health";

        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(healthEndpointUrl, String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"status\":\"UP\"");
    }

    @Test
    @DisplayName("Protected endpoint returns 401 Unauthorized for anonymous user")
    void protectedEndpoint_withoutAuth_returns401() {
        // Arrange
        // This endpoint is protected by Spring Security.
        String protectedUrl = "http://localhost:" + port + "/api/questionnaires";

        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(protectedUrl, String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
