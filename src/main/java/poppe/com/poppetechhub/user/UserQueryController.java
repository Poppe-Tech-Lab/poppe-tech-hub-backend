package poppe.com.poppetechhub.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import poppe.com.poppetechhub.user.dto.UserResponse;

@Tag(name = "Usuários", description = "Consulta de usuários")
@SecurityRequirement(name = "basicAuth")
@Validated
@RestController
@RequestMapping("/api/v1/users")
public class UserQueryController {

	private final UserService userService;

	public UserQueryController(UserService userService) {
		this.userService = userService;
	}

	@Operation(summary = "Consulta um usuário por ID")
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "Usuário encontrado",
			content = @Content(schema = @Schema(implementation = UserResponse.class))
		),
		@ApiResponse(
			responseCode = "400",
			description = "Identificador inválido",
			content = @Content(schema = @Schema(implementation = ProblemDetail.class))
		),
		@ApiResponse(responseCode = "401", description = "Não autenticado"),
		@ApiResponse(
			responseCode = "404",
			description = "Usuário não encontrado",
			content = @Content(schema = @Schema(implementation = ProblemDetail.class))
		)
	})
	@GetMapping("/{id}")
	public UserResponse findById(@PathVariable @Positive Long id) {
		return UserResponse.from(userService.findById(id));
	}

}
