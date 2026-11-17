# API de Plataformas Elevatórias (PTA)

API REST em Java 21 + Spring Boot 3.3 para gestão de categorias e plataformas elevatórias.

## Como rodar

1. Subir o PostgreSQL: `docker compose up -d`
2. Rodar a API: `mvn spring-boot:run`
3. Swagger: http://localhost:8080/swagger-ui.html

Variáveis opcionais: `DB_URL`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET` (mín. 32 caracteres).

Testes (usam H2 em memória, não precisam do Postgres): `mvn test`

## Usuários criados na primeira execução

| Perfil | E-mail | Senha |
|---|---|---|
| ADMIN | admin@artemis.com | admin123 |
| COMUM | comum@artemis.com | comum123 |

## Autenticação

`POST /auth/login` com `{"email": "...", "senha": "..."}` devolve um token JWT.
Envie no header `Authorization: Bearer <token>` (no Swagger, botão **Authorize**).
`POST /auth/registrar` cria um usuário COMUM.

| Operação | ADMIN | COMUM |
|---|---|---|
| GET (listar, buscar, filtros) | sim | sim |
| POST, PUT, DELETE | sim | não (403) |

## Endpoints

**Categorias** (`/categorias`): `POST`, `GET`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}`
- Filtro no path: `GET /categorias/{id}/plataformas`

**Plataformas** (`/plataformas`): `POST`, `GET`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}`
- Múltiplos parâmetros (todos opcionais): `GET /plataformas?modelo=genie&status=DISPONIVEL&categoriaId=1&alturaMin=6`

Exemplo de corpo para `POST /plataformas`:

```json
{
  "codigo": "PTA.1",
  "modelo": "Genie GS-1932",
  "alturaTrabalho": 7.8,
  "capacidadeKg": 230,
  "status": "DISPONIVEL",
  "categoriaId": 1
}
```

`status`: `DISPONIVEL`, `LOCADA` ou `MANUTENCAO`.

## Arquitetura

```
controller  -> recebe/devolve DTOs (records), sem regra de negócio
service     -> regras de negócio e transações
repository  -> Spring Data JPA (JpaRepository + Specification)
model       -> entidades JPA (nunca expostas na API)
dto         -> records de request/response
security    -> JWT (HS256), perfis ADMIN/COMUM
```

- Relacionamento 1:N: `Categoria` 1 → N `Plataforma`.
- Regras de negócio (service): nome de categoria e código de plataforma únicos (409); categoria com plataformas não pode ser excluída (409); plataforma `LOCADA` não pode ser excluída (409); categoria inexistente ao salvar plataforma (404).
- Erros padronizados em `ProblemDetail` (400, 401, 403, 404, 409).
