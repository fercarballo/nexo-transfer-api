# nexo-transfer-api

Fictitious transfers API for the **Nexo Finanzas** ecosystem — **secure and traceable**. It is the
core of the portfolio: it defines the contract that the web and mobile channels depend on.

![CI](https://github.com/fercarballo/nexo-transfer-api/actions/workflows/ci.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3-6DB33F?logo=springboot&logoColor=white)

> ⚠️ **Fictitious data.** Accounts, users, balances and transfers are for demonstration only and do
> not represent any real entity, person or system.

## In 5 minutes

**What it is.** A REST service that executes transfers between accounts with the guarantees expected
in the financial sector: **validation**, **ownership-based authorization**, **idempotency** (a retry
never duplicates a movement) and **traceability** (every request carries a correlation id).

**Prerequisites.** JDK 21 and Maven (or just Docker).

```bash
# Local
mvn test                                  # 12/12 tests (unit + BDD)
mvn -DskipTests package
java -jar target/nexo-transfer-api-0.1.0.jar
# Container
docker compose up --build

# Verify
curl -H "Authorization: Bearer tok-ana-local-dev" \
  http://localhost:8080/api/v1/accounts/acc-ana-001/balance
```

Interactive docs (OpenAPI/Swagger): `http://localhost:8080/swagger-ui.html`.

## Endpoints

| Method | Path | Description | Auth |
|---|---|---|---|
| `POST` | `/api/v1/transfers` | Create a transfer (optional `Idempotency-Key` header) | Bearer |
| `GET` | `/api/v1/transfers/{id}` | Fetch a transfer (only if the user is involved) | Bearer |
| `GET` | `/api/v1/accounts/{id}/balance` | Balance of an owned account | Bearer |

## Business rules (summary)

- The source account must **exist** and be **owned** by the authenticated user (else `403`).
- Distinct source/destination; matching currencies; amount > 0.
- **Sufficient funds** (else `422 INSUFFICIENT_FUNDS`).
- **Idempotency:** same `Idempotency-Key` + same body → returns the original transfer;
  same key + different body → `409 IDEMPOTENCY_CONFLICT`.

## Testing

- Unit tests for domain rules (fast, no server).
- BDD (Cucumber + REST-assured) end-to-end over real HTTP.
- Postman/Newman collection with assertions.

The full documentation set is in Spanish under [`docs/`](docs/). This file is a summary; see
[`README.md`](README.md) for the complete, layered documentation.

## License

MIT — see [`LICENSE`](LICENSE).
