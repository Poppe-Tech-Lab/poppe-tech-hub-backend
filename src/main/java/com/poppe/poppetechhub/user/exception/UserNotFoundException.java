package com.poppe.poppetechhub.user.exception;

public class UserNotFoundException extends RuntimeException {

	public UserNotFoundException(Long id) {
		super("Usuário não encontrado: " + id);
	}

}
