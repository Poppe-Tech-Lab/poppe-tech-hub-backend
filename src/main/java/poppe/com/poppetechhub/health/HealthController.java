package poppe.com.poppetechhub.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint público usado para verificar se a aplicação está no ar.
 * Não consulta banco nem dependências externas: serve apenas como "sinal de vida".
 */
@RestController
@RequestMapping("/health")
public class HealthController {

	@GetMapping
	public HealthResponse health() {
		return new HealthResponse("UP");
	}

	public record HealthResponse(String status) {
	}

}
