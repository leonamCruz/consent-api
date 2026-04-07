# 📘 Desafio Técnico| API de Consentimentos

## 👋 Visão Geral
Este projeto foi desenvolvido como parte de um **desafio técnico** 💙.

A proposta é disponibilizar uma API REST para gerenciamento de consentimentos de uso de dados, com foco em:

- ✅ modelagem simples e coesa
- ✅ boas práticas de API REST
- ✅ validação de entrada
- ✅ rastreabilidade de alterações
- ✅ testes automatizados
- ✅ execução fácil com Docker

---

## 🧰 Stack Utilizada

- ☕ Java 21
- 🌱 Spring Boot 4
- 🍃 MongoDB
- 🧪 JUnit 5 + Mockito
- 📦 Testcontainers
- 📚 Swagger / OpenAPI
- 🐳 Docker + Docker Compose
- ✍️ Lombok

---

## 🚀 Como subir o projeto com Docker Compose

### ✅ Pré-requisitos

Antes de tudo, você precisa ter instalado:

- 🐳 Docker
- 🐳 Docker Compose Plugin

### ▶️ Subindo a aplicação

Na raiz do projeto, execute:

```bash
docker compose up --build
```

Esse comando irá:

- 🏗️ buildar a aplicação Java
- 🍃 subir o MongoDB
- 🔗 conectar a API ao banco
- 🌍 expor a aplicação na porta `8080`

### 🔎 URLs importantes

Depois que os containers subirem, acesse:

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### 🛑 Parando o ambiente

Para parar:

```bash
docker compose down
```

Se quiser remover também o volume do Mongo:

```bash
docker compose down -v
```

---

## 🧪 Como rodar os testes

Para executar a suíte de testes:

```bash
./mvnw test
```

Os testes cobrem:

- ✅ regras de serviço
- ✅ validações da API
- ✅ fluxo completo dos endpoints
- ✅ integração com MongoDB usando Testcontainers

---

## 📌 Endpoints Principais

### `POST /consents`
Cria um consentimento.

### `GET /consents`
Lista consentimentos com paginação.

### `GET /consents/{id}`
Busca um consentimento por ID.

### `PUT /consents/{id}`
Atualiza um consentimento existente.

### `DELETE /consents/{id}`
Remove um consentimento e registra auditoria.

---

## 📨 Exemplo de payload

```json
{
  "cpf": "12345678909",
  "status": "ACTIVE",
  "additionalInfo": "primeiro consentimento"
}
```

---

## 🧠 Principais Conceitos Aplicados

### 🏛️ Arquitetura em camadas
O projeto foi organizado separando responsabilidades entre:

- `controller` 🎯 para entrada HTTP
- `service` 🧩 para regras de negócio
- `repository` 🗄️ para acesso ao MongoDB
- `domain/dto` 📦 para contratos e entidades
- `exception` 🚨 para tratamento padronizado de erros

Isso deixa o código mais legível, mais fácil de testar e mais simples de evoluir.

### ✅ Validação de dados
O payload de entrada usa **Bean Validation** para garantir:

- CPF obrigatório
- CPF válido
- status obrigatório
- `additionalInfo` com tamanho controlado

Assim, a API rejeita entradas inválidas logo na borda.

### 🆔 CPF único no banco
O campo `cpf` possui **índice único no MongoDB** 🔒.

Isso garante integridade de dados e evita que dois consentimentos diferentes sejam gravados para o mesmo CPF.

### ♻️ Idempotência no `POST`
Foi aplicada uma regra de idempotência baseada no CPF:

- se chegar o **mesmo CPF com o mesmo payload**, a API retorna o mesmo consentimento já existente
- se chegar o **mesmo CPF com dados diferentes**, a API retorna `409 Conflict`

Na prática, isso evita a geração de vários `UUIDs` para o mesmo pedido repetido.

### 🕵️ Rastreabilidade / auditoria
As operações de `PUT` e `DELETE` geram registros de auditoria 📝.

Isso permite acompanhar:

- estado anterior
- estado posterior
- data da alteração
- tipo de ação executada

### 📄 Paginação
O endpoint `GET /consents` suporta paginação com `Pageable`, facilitando consultas maiores sem retornar tudo de uma vez.

### 📚 Documentação automática
A API expõe documentação via Swagger/OpenAPI, o que facilita:

- entender contratos
- testar endpoints
- explorar respostas e códigos HTTP

### 🧪 Estratégia de testes
Foram aplicados dois níveis de teste:

- **unitários** 🧪, validando regras da camada de serviço
- **integração** 🔗, validando a API e a persistência com Mongo real via Testcontainers

---

## ⚙️ Regras de Negócio Importantes

- 📌 `cpf` deve ser único
- 📌 `creationDateTime` é gerado automaticamente
- 📌 `POST` idempotente para requisições repetidas
- 📌 `PUT` não permite usar um CPF já pertencente a outro consentimento
- 📌 `DELETE` remove o consentimento e registra auditoria

---

## 📂 Estrutura do Projeto

```text
src/main/java/top/lmix/consentimento
├── config
├── controller
├── domain
│   ├── dto
│   ├── entity
│   ├── enums
│   └── repository
├── exception
└── service
```

---

## 💡 Diferenciais Implementados

- 🚀 documentação com Swagger
- 🐳 ambiente pronto com Docker Compose
- 🧪 testes automatizados com Testcontainers
- 🕵️ trilha de auditoria
- ♻️ idempotência no endpoint de criação
- 🔒 unicidade de CPF no banco
- 📄 paginação no endpoint de listagem

---

## 🙌 Considerações Finais

Este projeto busca entregar uma solução simples, objetiva e consistente para o desafio técnico, aplicando fundamentos importantes de engenharia de software como:

- clareza de responsabilidades
- integridade de dados
- previsibilidade de comportamento
- testabilidade
- facilidade de execução local

Se quiser explorar a API rapidamente, a melhor porta de entrada é o Swagger UI 📚:

`http://localhost:8080/swagger-ui.html`
