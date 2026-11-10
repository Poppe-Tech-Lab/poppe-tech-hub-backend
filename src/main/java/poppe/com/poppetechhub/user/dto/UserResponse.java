package poppe.com.poppetechhub.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import poppe.com.poppetechhub.user.User;

import java.time.Instant;

@Schema(description = "Dados públicos de um usuário")
public record UserResponse(

	@Schema(description = "Identificador do usuário", example = "1")
	Long id,

	@Schema(description = "E-mail normalizado", example = "dev@example.com")
	String email,

	@Schema(description = "Indica se a conta está habilitada", example = "true")
	boolean enabled,

	@Schema(description = "Data de criação da conta", example = "2026-11-09T12:00:00Z")
	Instant createdAt

) {

	public static UserResponse from(User user) {
		return new UserResponse(
			user.getId(),
			user.getEmail(),
			user.isEnabled(),
			user.getCreatedAt()
		);
	}

}
