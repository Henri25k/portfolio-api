# Portfolio API

API REST para gerenciamento do portfólio de projetos de uma empresa.

## Tecnologias

- Java 21
- Spring Boot
- Spring Data JPA / Hibernate
- PostgreSQL
- Spring Security com Basic Auth
- Swagger / OpenAPI
- JUnit 5, Mockito e JaCoCo
- Maven

## Arquitetura

O projeto utiliza arquitetura MVC e separa as responsabilidades em:

- `controller`: endpoints REST;
- `service`: regras de negócio;
- `repository`: acesso aos dados;
- `entity`: entidades persistidas;
- `dto`: objetos de entrada e resposta da API;
- `mapper`: conversão de entidades em DTOs;
- `exception`: tratamento global de erros;
- `client`: integração com a API externa mockada de membros;
- `specification`: filtros da listagem de projetos.

## Pré-requisitos

- JDK 21;
- PostgreSQL em execução na porta `5432`;
- Maven, ou uso do Maven Wrapper incluído no projeto;
- Projeto `member-api-mock` em execução na porta `9090`.

## Banco de dados

Crie o banco:

```sql
CREATE DATABASE portfolio_db;