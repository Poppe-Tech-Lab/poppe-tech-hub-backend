package com.poppe.poppetechhub.user.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PasswordPolicyTest {

	@Test
	void acceptsPasswordWithMinimumLength() {
		assertThatCode(() -> PasswordPolicy.validate("12345678"))
			.doesNotThrowAnyException();
	}

	@Test
	void acceptsPasswordWithMaximumBytes() {
		assertThatCode(() -> PasswordPolicy.validate("a".repeat(72)))
			.doesNotThrowAnyException();
	}

	@Test
	void rejectsNullBlankAndShortPassword() {
		assertThatThrownBy(() -> PasswordPolicy.validate(null))
			.isInstanceOf(InvalidPasswordException.class)
			.hasMessage("Senha é obrigatória");

		assertThatThrownBy(() -> PasswordPolicy.validate("   "))
			.isInstanceOf(InvalidPasswordException.class)
			.hasMessage("Senha é obrigatória");

		assertThatThrownBy(() -> PasswordPolicy.validate("1234567"))
			.isInstanceOf(InvalidPasswordException.class)
			.hasMessageContaining("pelo menos");
	}

	@Test
	void rejectsPasswordThatWouldBeTruncatedByBcrypt() {
		String password = "😀".repeat(19); // 19 caracteres, 76 bytes em UTF-8

		assertThatThrownBy(() -> PasswordPolicy.validate(password))
			.isInstanceOf(InvalidPasswordException.class)
			.hasMessageContaining("72 bytes");
	}

}
