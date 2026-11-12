package com.poppe.poppetechhub.user.domain;

/**
 * Violação da regra de formato/normalização de e-mail.
 * É uma exceção de domínio para que o serviço não dependa de detalhes de HTTP.
 */
public class InvalidEmailException extends RuntimeException {

	public InvalidEmailException(String message) {
		super(message);
	}

}
