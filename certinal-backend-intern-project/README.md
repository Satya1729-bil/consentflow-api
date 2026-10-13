# ConsentFlow API — Backend Engineering Portfolio Project

A Java Spring Boot backend that models a digital consent / document approval workflow. It is an independent learning project inspired by common enterprise eConsent/eSignature patterns; it is not Certinal's software and does not perform legally valid digital signatures.

## Tech stack

- Java 21
- Spring Boot 3.4
- Spring Web / REST
- Spring Data JPA + Hibernate
- PostgreSQL
- Flyway database migrations
- Bean Validation
- Spring Boot Actuator health endpoint
- Springdoc OpenAPI / Swagger UI
- JUnit 5 / Spring Boot Test
- Docker + Docker Compose
- Maven

## Implemented features

- Create and list consent workflows
- Add recipients with unique email per workflow and a signing order
- Send only draft workflows that have at least one recipient
- Record recipient viewed / consented / declined status
- Complete workflow once all recipients have submitted a final response
- Cancel a workflow subject to state rules
- Persist workflow event audit trail
- Validate inputs and return structured API errors
- PostgreSQL schema managed with Flyway
- Indexes on status, recipient workflow, and audit-event time
- Docker Compose local development environment

## Run with Docker

Requires Docker Desktop.

```powershell
docker compose up --build
```

Open Swagger UI: http://localhost:8080/docs  
Health endpoint: http://localhost:8080/actuator/health

Stop with `Ctrl+C`; then:

```powershell
docker compose down
```

To delete local DB data as well: `docker compose down -v`.

The credentials in `docker-compose.yml` are development-only. Never use them in a public deployment.

## Example API flow

### 1. Create workflow

`POST /api/v1/workflows`

```json
{
  "title": "Patient Intake Consent",
  "createdBy": "satya@example.com"
}
```

### 2. Add recipient

`POST /api/v1/workflows/1/recipients`

```json
{
  "name": "Asha Rao",
  "email": "asha@example.com",
  "signingOrder": 1
}
```

### 3. Send workflow

`POST /api/v1/workflows/1/send?actor=satya@example.com`

### 4. Record recipient response

`POST /api/v1/workflows/1/recipients/1/response?status=CONSENTED`

Supported response statuses: `VIEWED`, `CONSENTED`, `DECLINED`.

### 5. View audit events

`GET /api/v1/workflows/1/audit-events`

## Workflow states

- `DRAFT`: workflow is being prepared; recipients may be added.
- `SENT`: workflow was sent and can receive recipient responses.
- `IN_PROGRESS`: at least one recipient has responded but not all have a final response.
- `COMPLETED`: every recipient has consented or declined.
- `CANCELLED`: workflow was cancelled before completion.

## Architecture

Controller -> Service -> Repository -> PostgreSQL.

The service layer owns workflow state transitions and writes audit events in the same database transaction as the related operation.

## Important limitations and next steps

This is a starter portfolio project, not a production-ready or legally compliant e-signature product. It does not implement identity verification, cryptographic signatures, authorization, encrypted document storage, email delivery, webhooks, or tenant isolation. Do not use real patient data or real confidential documents.

Recommended next improvements:
1. Add Spring Security authentication and role/tenant-based authorization.
2. Add document metadata and secure object storage (do not store sensitive files in the database).
3. Add webhook delivery with retries and idempotency keys.
4. Add integration tests using Testcontainers and verify state-transition edge cases.
5. Add structured logging, metrics, rate limits, and CI workflow.
6. Deploy to a cloud environment with managed PostgreSQL and secrets management.
7. Add pagination and filtering to workflow listing.
8. Add optimistic locking to protect against concurrent updates.

## Resume description — only after validating the implementation

**ConsentFlow API | Java, Spring Boot, PostgreSQL, Spring Data JPA, Flyway, Docker**
- Built REST APIs to manage consent workflows, recipients, workflow state transitions, and chronological audit events.
- Implemented request validation, relational constraints, database migrations, and service-layer rules for workflow lifecycle management.
- Containerized the service and PostgreSQL for reproducible local development; documented endpoints with OpenAPI/Swagger.
