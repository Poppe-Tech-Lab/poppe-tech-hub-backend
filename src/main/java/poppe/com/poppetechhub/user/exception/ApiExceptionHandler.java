package poppe.com.poppetechhub.user.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import poppe.com.poppetechhub.user.domain.InvalidEmailException;
import poppe.com.poppetechhub.user.domain.InvalidPasswordException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Converte erros de entrada e regras de negócio em respostas RFC 9457
 * (application/problem+json), sem expor SQL, stack trace ou dados internos.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ProblemDetail handleInvalidBody(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new LinkedHashMap<>();
		for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
			errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
		}

		ProblemDetail problem = ProblemDetail.forStatusAndDetail(
			HttpStatus.BAD_REQUEST,
			"Dados inválidos na requisição"
		);
		problem.setTitle("Requisição inválida");
		problem.setProperty("errors", errors);
		return problem;
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
		Map<String, String> errors = new LinkedHashMap<>();
		ex.getConstraintViolations().forEach(violation ->
			errors.put(violation.getPropertyPath().toString(), violation.getMessage())
		);

		ProblemDetail problem = ProblemDetail.forStatusAndDetail(
			HttpStatus.BAD_REQUEST,
			"Parâmetros inválidos na requisição"
		);
		problem.setTitle("Requisição inválida");
		problem.setProperty("errors", errors);
		return problem;
	}

	@ExceptionHandler(HandlerMethodValidationException.class)
	public ProblemDetail handleMethodValidation(HandlerMethodValidationException ex) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(
			HttpStatus.BAD_REQUEST,
			"Parâmetros inválidos na requisição"
		);
		problem.setTitle("Requisição inválida");
		return problem;
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ProblemDetail handleUnreadableBody(HttpMessageNotReadableException ex) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(
			HttpStatus.BAD_REQUEST,
			"Corpo da requisição inválido ou malformado"
		);
		problem.setTitle("Requisição inválida");
		return problem;
	}

	@ExceptionHandler({ InvalidEmailException.class, InvalidPasswordException.class })
	public ProblemDetail handleInvalidUserData(RuntimeException ex) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(
			HttpStatus.BAD_REQUEST,
			ex.getMessage()
		);
		problem.setTitle("Dados do usuário inválidos");
		return problem;
	}

	@ExceptionHandler(DuplicateEmailException.class)
	public ProblemDetail handleDuplicateEmail(DuplicateEmailException ex) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(
			HttpStatus.CONFLICT,
			ex.getMessage()
		);
		problem.setTitle("Conflito de e-mail");
		return problem;
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ProblemDetail handleUserNotFound(UserNotFoundException ex) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(
			HttpStatus.NOT_FOUND,
			"Usuário não encontrado"
		);
		problem.setTitle("Recurso não encontrado");
		return problem;
	}

}
