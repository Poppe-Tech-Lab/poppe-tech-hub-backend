package poppe.com.poppetechhub.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste de integração real: sobe a aplicação em porta aleatória, aplica as
 * migrations no PostgreSQL efêmero e faz chamadas HTTP com Basic Auth.
 * Não usa MockMvc; o objetivo é validar o fluxo completo pela rede.
 */
@SpringBootTest(
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
	properties = {
		"app.users.direct-registration-enabled=true",
		"spring.security.user.name=test",
		"spring.security.user.password=test"
	}
)
@Testcontainers(disabledWithoutDocker = true)
class UserIntegrationTest {

	private static final String PASSWORD = "senha-segura-123";
	private static final Pattern ID_PATTERN = Pattern.compile("\"id\"\\s*:\\s*(\\d+)");

	@Container
	@ServiceConnection
	static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17-alpine");

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private PasswordHasher passwordHasher;

	@LocalServerPort
	private int port;

	private final HttpClient httpClient = HttpClient.newHttpClient();

	@BeforeEach
	void cleanDatabase() {
		userRepository.deleteAll();
	}

	@Test
	void createsAndRetrievesUserWithoutExposingSensitiveData() throws Exception {
		String email = uniqueEmail();

		HttpResponse<String> created = post(email, PASSWORD, true);

		assertThat(created.statusCode()).isEqualTo(201);
		assertThat(created.headers().firstValue("location").orElse(""))
			.startsWith("/api/v1/users/");
		assertThat(created.body())
			.contains("\"email\":\"" + email + "\"")
			.contains("\"enabled\":true")
			.doesNotContain("password")
			.doesNotContain("hash")
			.doesNotContain(PASSWORD);

		long id = extractId(created.body());

		Map<String, Object> row = jdbcTemplate.queryForMap(
			"SELECT email, password_hash FROM users WHERE id = ?", id
		);
		assertThat(row.get("email")).isEqualTo(email);

		String hash = (String) row.get("password_hash");
		assertThat(hash)
			.isNotEqualTo(PASSWORD)
			.startsWith("$2");
		assertThat(passwordHasher.matches(PASSWORD, hash)).isTrue();

		HttpResponse<String> fetched = get("/api/v1/users/" + id, true);

		assertThat(fetched.statusCode()).isEqualTo(200);
		assertThat(fetched.body())
			.contains("\"id\":" + id)
			.contains("\"email\":\"" + email + "\"")
			.doesNotContain("password")
			.doesNotContain("hash")
			.doesNotContain(PASSWORD);
	}

	@Test
	void rejectsDuplicateEmailIgnoringCaseAndWhitespace() throws Exception {
		String email = uniqueEmail();

		assertThat(post(email, PASSWORD, true).statusCode()).isEqualTo(201);

		String duplicate = "  " + email.toUpperCase(Locale.ROOT) + "  ";
		HttpResponse<String> response = post(duplicate, PASSWORD, true);

		assertThat(response.statusCode()).isEqualTo(409);
		assertThat(response.body()).contains("E-mail já cadastrado");
		assertThat(userRepository.count()).isEqualTo(1);
	}

	@Test
	void rejectsInvalidInput() throws Exception {
		HttpResponse<String> invalidEmail = post("nao-e-email", PASSWORD, true);
		assertThat(invalidEmail.statusCode()).isEqualTo(400);
		assertThat(invalidEmail.body()).contains("E-mail inválido");

		HttpResponse<String> shortPassword = post(uniqueEmail(), "123", true);
		assertThat(shortPassword.statusCode()).isEqualTo(400);
		assertThat(shortPassword.body()).contains("Senha deve ter");
	}

	@Test
	void requiresAuthenticationForCreationAndQuery() throws Exception {
		assertThat(post(uniqueEmail(), PASSWORD, false).statusCode()).isEqualTo(401);
		assertThat(get("/api/v1/users/1", false).statusCode()).isEqualTo(401);
	}

	@Test
	void returnsNotFoundForUnknownUser() throws Exception {
		HttpResponse<String> response = get("/api/v1/users/999999", true);

		assertThat(response.statusCode()).isEqualTo(404);
		assertThat(response.body()).contains("Usuário não encontrado");
	}

	@Test
	void documentsUserEndpointsInOpenApiWithoutExposingPasswordHash() throws Exception {
		HttpResponse<String> response = get("/v3/api-docs", false);

		assertThat(response.statusCode()).isEqualTo(200);
		assertThat(response.body())
			.contains("/api/v1/users")
			.doesNotContain("passwordHash");
	}

	private HttpResponse<String> post(String email, String password, boolean authenticated) throws Exception {
		String body = """
			{"email":"%s","password":"%s"}
			""".formatted(email, password);

		HttpRequest.Builder builder = HttpRequest.newBuilder()
			.uri(URI.create(baseUrl() + "/api/v1/users"))
			.header("Content-Type", "application/json")
			.POST(HttpRequest.BodyPublishers.ofString(body));

		if (authenticated) {
			builder.header("Authorization", basicAuth());
		}

		return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
	}

	private HttpResponse<String> get(String path, boolean authenticated) throws Exception {
		HttpRequest.Builder builder = HttpRequest.newBuilder()
			.uri(URI.create(baseUrl() + path))
			.GET();

		if (authenticated) {
			builder.header("Authorization", basicAuth());
		}

		return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
	}

	private String baseUrl() {
		return "http://localhost:" + port;
	}

	private String basicAuth() {
		String credentials = "test:test";
		return "Basic " + Base64.getEncoder()
			.encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
	}

	private long extractId(String body) {
		Matcher matcher = ID_PATTERN.matcher(body);
		assertThat(matcher.find()).as("id no corpo da resposta").isTrue();
		return Long.parseLong(matcher.group(1));
	}

	private String uniqueEmail() {
		return "user-" + UUID.randomUUID() + "@example.com";
	}

}
