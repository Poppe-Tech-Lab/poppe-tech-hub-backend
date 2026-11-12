-- Primeira migration de negócio: cria a tabela de usuários.
-- O e-mail é armazenado normalizado (trim + minúsculas). O índice em lower(email)
-- e o CHECK reforçam a unicidade mesmo se algum caminho de código esquecer a normalização.
CREATE TABLE users (
	id BIGSERIAL PRIMARY KEY,
	email VARCHAR(320) NOT NULL,
	password_hash VARCHAR(255) NOT NULL,
	enabled BOOLEAN NOT NULL DEFAULT TRUE,
	created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
	updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
	CONSTRAINT ck_users_email_lower CHECK (email = lower(email))
);

CREATE UNIQUE INDEX uq_users_email_lower ON users (lower(email));
