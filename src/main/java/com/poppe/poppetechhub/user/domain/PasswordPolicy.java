package com.poppe.poppetechhub.user.domain;

import java.nio.charset.StandardCharsets;

/**
 * Regras de senha aplicadas antes do hash.
 *
 * <p>O BCrypt trunca entradas acima de 72 bytes. Validar o tamanho em bytes
 * evita aceitar uma senha que seria silenciosamente cortada na verificação.
 * Não aplicamos trim: espaços podem fazer parte da senha.
 */
public final class PasswordPolicy {

	public static final int MIN_LENGTH = 8;
	public static final int MAX_BCRYPT_BYTES = 72;

	private PasswordPolicy() {
	}

	public static void validate(String password) {
		if (password == null || password.isBlank()) {
			throw new InvalidPasswordException("Senha é obrigatória");
		}
		if (password.length() < MIN_LENGTH) {
			throw new InvalidPasswordException("Senha deve ter pelo menos " + MIN_LENGTH + " caracteres");
		}
		if (password.getBytes(StandardCharsets.UTF_8).length > MAX_BCRYPT_BYTES) {
			throw new InvalidPasswordException("Senha deve ter no máximo " + MAX_BCRYPT_BYTES + " bytes");
		}
	}

}
