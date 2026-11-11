package com.poppe.poppetechhub.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.poppe.poppetechhub.user.domain.EmailAddress;

@Schema(description = "Dados para cadastro direto de um usuário")
public record CreateUserRequest(

	@NotBlank(message = "E-mail é obrigatório")
	@Email(message = "E-mail inválido")
	@Size(max = 320, message = "E-mail deve ter no máximo 320 caracteres")
	@Schema(description = "E-mail do usuário", example = "dev@example.com", maxLength = 320)
	String email,

	@NotBlank(message = "Senha é obrigatória")
	@Size(min = 8, max = 72, message = "Senha deve ter entre 8 e 72 caracteres")
	@Schema(
		description = "Senha em texto puro; usada apenas para gerar o hash e nunca armazenada ou retornada",
		example = "senha-segura",
		minLength = 8,
		maxLength = 72,
		accessMode = Schema.AccessMode.WRITE_ONLY,
		format = "password"
	)
	String password

) {

	public CreateUserRequest {
		// Normaliza antes da validação para que espaços e maiúsculas não
		// impeçam um e-mail válido de ser aceito.
		email = EmailAddress.normalize(email);
	}

	@Override
	public String toString() {
		return "CreateUserRequest[email=" + email + ", password=***]";
	}

}
