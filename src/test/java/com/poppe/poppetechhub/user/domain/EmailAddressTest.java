package com.poppe.poppetechhub.user.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailAddressTest {

	@Test
	void normalizesTrimmingAndLowerCase() {
		EmailAddress email = EmailAddress.of("  User.Name+tag@Example.COM  ");

		assertThat(email.value()).isEqualTo("user.name+tag@example.com");
	}

	@Test
	void preservesDotsAndPlusBecauseTheyAreProviderSpecificRules() {
		EmailAddress email = EmailAddress.of("first.last+mentoria@example.com");

		assertThat(email.value()).isEqualTo("first.last+mentoria@example.com");
	}

	@Test
	void acceptsUppercaseDomainAndLocalPart() {
		EmailAddress email = EmailAddress.of("STUDENT@EXAMPLE.COM");

		assertThat(email.value()).isEqualTo("student@example.com");
	}

	@Test
	void rejectsNull() {
		assertThatThrownBy(() -> EmailAddress.of(null))
			.isInstanceOf(InvalidEmailException.class)
			.hasMessage("E-mail é obrigatório");
	}

	@Test
	void rejectsBlank() {
		assertThatThrownBy(() -> EmailAddress.of("   "))
			.isInstanceOf(InvalidEmailException.class)
			.hasMessage("E-mail é obrigatório");
	}

	@Test
	void rejectsInvalidFormats() {
		assertThatThrownBy(() -> EmailAddress.of("nao-e-email"))
			.isInstanceOf(InvalidEmailException.class)
			.hasMessage("E-mail inválido");

		assertThatThrownBy(() -> EmailAddress.of("user@localhost"))
			.isInstanceOf(InvalidEmailException.class)
			.hasMessage("E-mail inválido");

		assertThatThrownBy(() -> EmailAddress.of("user@example"))
			.isInstanceOf(InvalidEmailException.class)
			.hasMessage("E-mail inválido");
	}

	@Test
	void rejectsEmailLongerThanMaximum() {
		String localPart = "a".repeat(310);
		String tooLong = localPart + "@example.com";

		assertThatThrownBy(() -> EmailAddress.of(tooLong))
			.isInstanceOf(InvalidEmailException.class)
			.hasMessageContaining("320");
	}

}
