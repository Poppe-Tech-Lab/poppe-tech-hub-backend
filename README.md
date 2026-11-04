# Poppe Tech Hub

**A plataforma do Poppe Tech Lab — projetos, pessoas, conhecimento e evolução profissional em um só lugar.**

> **Estado do projeto:** planejamento e desenvolvimento inicial. As funcionalidades abaixo descrevem o escopo previsto, não recursos já disponíveis em produção.

O **Poppe Tech Hub** é o primeiro produto desenvolvido pelo [Poppe Tech Lab](https://github.com/Poppe-Tech-Lab), uma iniciativa colaborativa de aprendizado prático em desenvolvimento de software.

O Hub terá uma **área pública** para apresentar a iniciativa e as experiências de seus participantes e uma **área interna** para apoiar o trabalho de mentoria, acompanhamento e documentação institucional.

## Por que construir o Hub?

A experiência de desenvolver software envolve mais do que programar: discutir requisitos, colaborar, revisar mudanças, testar, lidar com dificuldades e realizar entregas. O Poppe Tech Hub nasce para apoiar esse processo — e seu próprio desenvolvimento será o primeiro exercício prático do laboratório.

## Funcionalidades previstas

### Área pública

- Apresentação do Poppe Tech Lab e de seu funcionamento.
- Catálogo de projetos, descrições, tecnologias, status e links de demonstração.
- Diretório e perfis de participantes e mentores, **somente mediante autorização**.
- Histórico de projetos e contribuições divulgáveis, de forma progressiva.

### Área interna

- Acesso por convite, com permissões de **administrador, mentor e participante** (papéis acumuláveis).
- Administração de projetos e equipes; uma pessoa poderá colaborar em mais de um projeto.
- **Check-ins assíncronos:** entregas, aprendizados, dificuldades e próximos passos.
- **Reviews de mentoria:** avaliações descritivas, com rascunho, publicação e recomendações de evolução.
- Dashboards de participantes e mentores.
- **Central de Conhecimento:** documentação institucional em Markdown, legível por usuários autenticados e editável apenas por administradores.

Autoavaliações, PDI, contribuições verificáveis e integrações poderão ser acrescentados em incrementos posteriores. O escopo efetivo de cada entrega será refinado pela equipe.

## Privacidade por padrão

- Perfis públicos dependem de autorização e podem ter a visibilidade revogada.
- Check-ins, reviews e feedbacks individuais **não são públicos**.
- Permissões devem ser verificadas no backend, inclusive entre projetos e vínculos de mentoria.
- O projeto não pretende ranquear pessoas por produtividade ou frequência de check-ins.

## Como organizamos o trabalho

| Ferramenta         | Responsabilidade                                                                        |
| ------------------ | --------------------------------------------------------------------------------------- |
| **Trello**         | Requisitos de trabalho, backlog, sprints, atribuições e bugs (quadro interno da equipe) |
| **GitHub**         | Código-fonte, pull requests, testes, revisões e documentação técnica                    |
| **Poppe Tech Hub** | Experiências, projetos, perfis, check-ins, mentorias e documentação institucional       |

Issues públicas do GitHub servem para **bugs e propostas de melhoria que possam ser compartilhados publicamente**. O planejamento e a execução interna continuam no Trello; não é necessário duplicar cada tarefa nos dois lugares.

## Arquitetura e execução local

Este repositório contém o **backend** do Poppe Tech Hub: uma API em **Java 21 + Spring Boot**, com **PostgreSQL** e migrations versionadas com **Flyway**. O frontend vive em repositório próprio.

> **Importante:** execute os comandos a partir da **raiz do repositório**. É de lá que a aplicação lê o arquivo `.env`.

### Fluxo rápido

```bash
# 1. Criar o .env e definir DB_PASSWORD (apenas na primeira vez)
Copy-Item .env.example .env     # Windows (PowerShell)
cp .env.example .env            # Linux/macOS

# 2. Subir o PostgreSQL
docker compose up -d

# 3. Rodar a API
mvnw.cmd spring-boot:run        # Windows
./mvnw spring-boot:run          # Linux/macOS

# 4. Verificar o health-check (em outro terminal)
curl http://localhost:8080/health

# 5. Rodar os testes (Docker precisa estar ativo)
mvnw.cmd test                   # Windows
./mvnw test                     # Linux/macOS
```

Os detalhes de cada etapa estão nas seções abaixo.

### Pré-requisitos

- **JDK 21** (`java -version`)
- **Docker com Docker Compose** (`docker compose version`)
- Maven **não** é necessário: o projeto inclui o wrapper (`mvnw` / `mvnw.cmd`)

### 1. Configurar as variáveis de ambiente

Nenhum segredo é versionado. As configurações locais vêm de um arquivo `.env` (ignorado pelo Git):

```bash
cp .env.example .env        # Linux/macOS
Copy-Item .env.example .env # Windows (PowerShell)
```

Depois edite o `.env` e troque `DB_PASSWORD` por uma senha local. O mesmo arquivo é lido pelo Docker Compose **e** pela aplicação, então os valores ficam consistentes.

| Variável      | Padrão    | Descrição                                                  |
| ------------- | --------- | ---------------------------------------------------------- |
| `DB_NAME`     | `techhub` | Nome do banco                                              |
| `DB_USERNAME` | `techhub` | Usuário do banco                                           |
| `DB_PASSWORD` | —         | **Obrigatória**; sem valor padrão, por segurança           |
| `DB_PORT`     | `5432`    | Porta publicada no host (mude se a 5432 já estiver em uso)  |
| `DB_URL`      | —         | Opcional; sobrescreve a URL JDBC montada a partir das demais |
| `SERVER_PORT` | `8080`    | Porta HTTP da aplicação                                    |

Se preferir não usar `.env`, exporte as variáveis no terminal (`DB_PASSWORD=...`) antes de rodar. No PowerShell: `$env:DB_PASSWORD = "..."`.

### 2. Subir o PostgreSQL

```bash
docker compose up -d
docker compose ps   # o serviço "db" deve ficar "healthy"
```

Os dados ficam no volume nomeado `techhub-pgdata` e persistem entre reinícios.

Para parar o banco:

```bash
docker compose down      # para os containers, mantendo os dados
docker compose down -v   # para os containers e apaga os dados (recria do zero)
```

### 3. Rodar a aplicação

```bash
# Linux/macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

A aplicação sobe em `http://localhost:8080` (ou na porta definida em `SERVER_PORT`) e aplica as migrations pendentes automaticamente.

Health-check público:

```bash
curl http://localhost:8080/health
# {"status":"UP"}
```

No PowerShell, se preferir: `Invoke-RestMethod http://localhost:8080/health`.

> As demais rotas exigem autenticação (HTTP Basic). Em desenvolvimento, o Spring Security gera um usuário `user` com senha aleatória, impressa no log de inicialização.

### 4. Migrations (Flyway)

- Ficam em `src/main/resources/db/migration`.
- Nomenclatura: `V<numero>__descricao_em_snake_case.sql` (ex.: `V2__create_user_table.sql`).
- São aplicadas **automaticamente** na inicialização. O Hibernate está em `ddl-auto=validate`: ele apenas confere o mapeamento, nunca altera o schema.
- A migration atual, `V1__create_project_table.sql`, cria a tabela `project` e valida o pipeline de ponta a ponta.

**Para adicionar uma migration:** crie o próximo número, revise o SQL e suba a aplicação; o Flyway aplica o que ainda não foi executado. Commite o arquivo junto com a mudança de código que depende dele.

**Correção e retorno seguro:**

- **Nunca edite uma migration já aplicada.** O Flyway guarda o checksum e vai falhar com *checksum mismatch*. A correção vai em uma **nova** migration.
- Se uma migration falhar no meio, o histórico fica com uma entrada sem sucesso. Em ambiente local, o caminho mais simples é descartar o volume e reaplicar tudo:
  ```bash
  docker compose down -v
  docker compose up -d
  ./mvnw spring-boot:run
  ```
- `flyway repair` realinha a tabela de histórico (remove entradas de migrations falhas e atualiza checksums). Use com cautela e nunca como atalho para "fazer passar" em ambiente compartilhado.
- Em ambientes com dados reais, o retorno é **sempre** uma nova migration corretiva, revisada em PR. O Flyway Community não executa *undo* automático.

### 5. Testes

```bash
# Linux/macOS
./mvnw test

# Windows
mvnw.cmd test
```

Resultado esperado: `Tests run: 2, Failures: 0, Errors: 0, Skipped: 0`.

Há dois testes complementares para o health-check:

- `HealthControllerTest` — teste de camada web (`@WebMvcTest`), rápido e sem banco. Verifica que `GET /health` responde `200` com `{"status":"UP"}`, passando pela configuração de segurança.
- `HealthCheckIntegrationTest` — teste de integração de verdade (`@SpringBootTest` + porta aleatória). Sobe o contexto completo, um PostgreSQL efêmero via **Testcontainers** e aplica as migrations do Flyway; em seguida faz uma chamada HTTP real a `GET /health` sem autenticação. **Requer Docker.** Se o Docker não estiver disponível, este teste é ignorado automaticamente em vez de falhar.

Para rodar apenas o teste de integração:

```bash
mvnw.cmd -Dtest=HealthCheckIntegrationTest test   # Windows
./mvnw -Dtest=HealthCheckIntegrationTest test     # Linux/macOS
```

### 6. Estrutura relevante

```text
src/main/java/poppe/com/poppetechhub/
├── TechhubApplication.java      # classe main
├── config/SecurityConfig.java   # libera /health e protege o restante
└── health/HealthController.java # GET /health
src/main/resources/
├── application.properties       # configuração via variáveis de ambiente
└── db/migration/                # migrations Flyway
docker-compose.yml               # PostgreSQL local
.env.example                     # modelo das variáveis de ambiente
```

## Como contribuir

Nesta fase, as contribuições de código são restritas às pessoas participantes do projeto; pull requests externos não serão aceitos.

Bugs e sugestões de melhoria podem ser registrados em issues públicas para avaliação da equipe. Relatos de vulnerabilidades ou informações sensíveis não devem ser publicados em issues; o canal privado de reporte será divulgado antes do início das contribuições de código.

---

**Poppe Tech Lab — Da teoria à experiência real.**
