# Arquitectura — Diagramas de flujo

## Secuencia: creación de una transferencia (camino feliz)

```mermaid
sequenceDiagram
    participant C as Cliente
    participant F as Filtros (correlación + auth)
    participant Ctrl as TransferController
    participant Svc as TransferService
    participant Repo as Repositorios

    C->>F: POST /api/v1/transfers (Bearer, Idempotency-Key?)
    F->>F: asigna X-Request-Id
    F->>F: valida token → userId
    F->>Ctrl: request autenticada
    Ctrl->>Svc: createTransfer(command)
    Svc->>Repo: ¿existe la Idempotency-Key?
    Repo-->>Svc: no
    Svc->>Repo: carga origen y destino
    Svc->>Svc: valida reglas (titularidad, moneda, fondos)
    Svc->>Repo: debita origen, acredita destino, guarda transferencia
    Svc-->>Ctrl: Transfer (POSTED)
    Ctrl-->>C: 201 Created + TransferView
```

## Decisión: idempotencia

```mermaid
flowchart TD
    start["POST /transfers con Idempotency-Key"] --> exists{"¿La key ya existe?"}
    exists -->|No| process["Procesa la transferencia<br/>y la guarda con la key"]
    exists -->|"Sí, mismo cuerpo"| replay["Devuelve la transferencia<br/>original (no duplica)"]
    exists -->|"Sí, cuerpo distinto"| conflict["409 IDEMPOTENCY_CONFLICT"]
    style process fill:#1e7a4f,color:#fff
    style replay fill:#2e6da4,color:#fff
    style conflict fill:#c0392b,color:#fff
```

## Manejo de errores → códigos HTTP

| Excepción de dominio | HTTP | `code` |
|---|---|---|
| `AccountNotFound` | 404 | `ACCOUNT_NOT_FOUND` |
| `NotFound` (transferencia) | 404 | `NOT_FOUND` |
| `Forbidden` | 403 | `FORBIDDEN` |
| `Invalid` | 400 | `INVALID_REQUEST` |
| validación de entrada | 400 | `VALIDATION_ERROR` |
| `InsufficientFunds` | 422 | `INSUFFICIENT_FUNDS` |
| `IdempotencyConflict` | 409 | `IDEMPOTENCY_CONFLICT` |
| inesperada | 500 | `INTERNAL_ERROR` |
