package poppe.com.poppetechhub.user.exception;

public class DuplicateEmailException extends RuntimeException {

	public DuplicateEmailException() {
		super("E-mail já cadastrado");
	}

}
