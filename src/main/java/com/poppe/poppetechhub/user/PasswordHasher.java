package com.poppe.poppetechhub.user;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Encapsula o BCrypt sem expor um bean {@code PasswordEncoder}.
 *
 * <p>Se houvesse um bean {@code PasswordEncoder}, a auto-configuração de
 * segurança do Spring Boot assumiria que a senha do usuário de desenvolvimento
 * já estaria codificada e o Basic Auth local deixaria de funcionar. Quando o
 * login real for implementado, o bean de {@code PasswordEncoder} pode ser
 * introduzido junto com o {@code UserDetailsService} correspondente.
 */
@Component
public class PasswordHasher {

	private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	public String hash(String rawPassword) {
		return encoder.encode(rawPassword);
	}

	public boolean matches(String rawPassword, String passwordHash) {
		return encoder.matches(rawPassword, passwordHash);
	}

}
