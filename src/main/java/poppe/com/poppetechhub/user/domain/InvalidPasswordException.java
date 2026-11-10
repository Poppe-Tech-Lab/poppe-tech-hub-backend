package poppe.com.poppetechhub.user.domain;

/**
 * Violação das regras mínimas de senha antes da geração do hash.
 */
public class InvalidPasswordException extends RuntimeException {

	public InvalidPasswordException(String message) {
		super(message);
	}

}
