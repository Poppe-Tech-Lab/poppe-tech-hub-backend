package com.poppe.poppetechhub.user.exception;

public class DuplicateEmailException extends RuntimeException {

	public DuplicateEmailException() {
		super("E-mail já cadastrado");
	}

}
