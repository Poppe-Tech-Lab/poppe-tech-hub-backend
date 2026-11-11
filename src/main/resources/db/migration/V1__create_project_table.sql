-- Primeira migration do projeto: cria a tabela de projetos.
-- Ela existe para validar o pipeline de migrations ponta a ponta e será evoluída em migrations futuras.
-- Regra: este arquivo NÃO deve ser editado depois de aplicado (o Flyway valida o checksum).
CREATE TABLE project (
	id BIGSERIAL PRIMARY KEY,
	name VARCHAR(120) NOT NULL,
	description TEXT,
	created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
