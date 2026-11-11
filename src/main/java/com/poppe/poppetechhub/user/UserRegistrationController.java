package com.poppe.poppetechhub.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.poppe.poppetechhub.user.dto.CreateUserRequest;
import com.poppe.poppetechhub.user.dto.UserResponse;

import java.net.URI;

/**
 * Cadastro direto usado apenas em desenvolvimento ou em ambiente interno confiável.
 * A flag nasce desligada; o endpoint também continua exigindo autenticação Basic.
 * O cadastro por ADMIN autenticado substituirá este fluxo em um card futuro.
 */
@Tag(name = "Usuários", description = "Cadastro direto de usuários em desenvolvimento")
@SecurityRequirement(name = "basicAuth")
@RestController
@RequestMapping("/api/v1/users")
@ConditionalOnProperty(
	name = "app.users.direct-registration-enabled",
	havingValue = "true"
)
public class UserRegistrationController {

	private final UserService userService;

	public UserRegistrationController(UserService userService) {
		this.userService = userService;
	}

	@Operation(summary = "Cadastra um usuário diretamente (ambiente controlado)")
	@ApiResponses({
		@ApiResponse(
			responseCode = "201",
			description = "Usuário criado",
			content = @Content(schema = @Schema(implementation = UserResponse.class))
		),
		@ApiResponse(
			responseCode = "400",
			description = "Dados inválidos",
			content = @Content(schema = @Schema(implementation = ProblemDetail.class))
		),
		@ApiResponse(responseCode = "401", description = "Não autenticado"),
		@ApiResponse(
			responseCode = "409",
			description = "E-mail já cadastrado",
			content = @Content(schema = @Schema(implementation = ProblemDetail.class))
		)
	})
	@PostMapping
	public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
		UserResponse response = UserResponse.from(userService.create(request));
		URI location = URI.create("/api/v1/users/" + response.id());
		return ResponseEntity.created(location).body(response);
	}

}
