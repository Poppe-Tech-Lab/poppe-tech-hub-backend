package poppe.com.poppetechhub.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.ApplicationContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifica o padrão seguro do gate: sem configuração explícita, o cadastro
 * direto não é registrado. A consulta continua disponível.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Testcontainers(disabledWithoutDocker = true)
class UserRegistrationGateTest {

	@Container
	@ServiceConnection
	static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17-alpine");

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void directRegistrationIsDisabledByDefaultAndQueryRemainsAvailable() {
		assertThat(applicationContext.getBeanNamesForType(UserRegistrationController.class))
			.isEmpty();
		assertThat(applicationContext.getBeanNamesForType(UserQueryController.class))
			.hasSize(1);
	}

}
