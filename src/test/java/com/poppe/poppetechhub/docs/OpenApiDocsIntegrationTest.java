package com.poppe.poppetechhub.docs;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Garante que a especificação OpenAPI é gerada a partir do código e que o
 * health-check aparece nela. Sobe o contexto completo (web, segurança, JPA e
 * Flyway) contra um PostgreSQL real e efêmero, como o teste de integração do
 * health-check, e faz uma chamada HTTP real em {@code /v3/api-docs}.
 *
 * <p>Requer Docker. Se o Docker não estiver disponível, o teste é ignorado
 * (em vez de falhar), mantendo {@code mvnw test} utilizável em máquinas sem Docker.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
class OpenApiDocsIntegrationTest {

	@Container
	@ServiceConnection
	static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17-alpine");

	private final HttpClient httpClient = HttpClient.newHttpClient();

	@LocalServerPort
	private int port;

	@Test
	void generatedSpecContainsHealthEndpoint() throws Exception {
		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create("http://localhost:" + port + "/v3/api-docs"))
			.GET()
			.build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

		assertThat(response.statusCode()).isEqualTo(200);
		assertThat(response.headers().firstValue("content-type").orElse("")).startsWith("application/json");
		assertThat(response.body()).contains("\"/health\"");
	}

}
