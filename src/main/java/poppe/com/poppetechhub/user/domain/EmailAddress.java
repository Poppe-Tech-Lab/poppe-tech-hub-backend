package poppe.com.poppetechhub.user.domain;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Value object de e-mail.
 *
 * <p>A normalização é centralizada aqui para que criação, consulta e futuras
 * regras de convite usem exatamente a mesma representação. Não removemos pontos
 * nem sufixos "+": essas são convenções específicas de alguns provedores e não
 * regras gerais de e-mail.
 */
public record EmailAddress(String value) {

	private static final int MAX_LENGTH = 320;
	private static final Pattern EMAIL_PATTERN = Pattern.compile(
		"^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
		Pattern.CASE_INSENSITIVE
	);

	public EmailAddress {
		if (value == null) {
			throw new InvalidEmailException("E-mail é obrigatório");
		}

		String normalized = normalize(value);

		if (normalized.isBlank()) {
			throw new InvalidEmailException("E-mail é obrigatório");
		}
		if (normalized.length() > MAX_LENGTH) {
			throw new InvalidEmailException("E-mail deve ter no máximo " + MAX_LENGTH + " caracteres");
		}
		if (!EMAIL_PATTERN.matcher(normalized).matches()) {
			throw new InvalidEmailException("E-mail inválido");
		}

		value = normalized;
	}

	public static EmailAddress of(String rawValue) {
		return new EmailAddress(rawValue);
	}

	/**
	 * Normalização usada tanto pelo DTO (antes da validação Bean Validation)
	 * quanto pelo próprio value object.
	 */
	public static String normalize(String rawValue) {
		if (rawValue == null) {
			return null;
		}
		return rawValue.trim().toLowerCase(Locale.ROOT);
	}

	@Override
	public String toString() {
		return value;
	}

}
