package com.poppe.poppetechhub.user;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.poppe.poppetechhub.user.domain.EmailAddress;
import com.poppe.poppetechhub.user.domain.PasswordPolicy;
import com.poppe.poppetechhub.user.dto.CreateUserRequest;
import com.poppe.poppetechhub.user.exception.DuplicateEmailException;
import com.poppe.poppetechhub.user.exception.UserNotFoundException;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordHasher passwordHasher;

	public UserService(UserRepository userRepository, PasswordHasher passwordHasher) {
		this.userRepository = userRepository;
		this.passwordHasher = passwordHasher;
	}

	@Transactional
	public User create(CreateUserRequest request) {
		EmailAddress email = EmailAddress.of(request.email());
		PasswordPolicy.validate(request.password());

		if (userRepository.existsByEmail(email.value())) {
			throw new DuplicateEmailException();
		}

		User user = new User(email.value(), passwordHasher.hash(request.password()));

		try {
			return userRepository.save(user);
		}
		catch (DataIntegrityViolationException ex) {
			// O índice único também protege contra duas requisições concorrentes
			// que passaram pela checagem acima ao mesmo tempo.
			throw new DuplicateEmailException();
		}
	}

	@Transactional(readOnly = true)
	public User findById(Long id) {
		return userRepository.findById(id)
			.orElseThrow(() -> new UserNotFoundException(id));
	}

}
