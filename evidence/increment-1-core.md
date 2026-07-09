# Evidencia — Incremento 1: núcleo de la API (verificado)

Fecha: 2026-07-09 · Entorno: local (JDK 21.0.11, Maven 3.9.16, Spring Boot 3.3.5)

> Esta evidencia es reproducible. Ver "Cómo regenerar" al final.

## 1. Pruebas automatizadas — `mvn test`

Resultado real de la corrida:

```
Tests run: 7, Failures: 0, Errors: 0, Skipped: 0  -- com.nexo.transfer.TransferServiceTest
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0  -- com.nexo.transfer.RunCucumberTest
Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

- **7 pruebas unitarias** de las reglas de dominio (`TransferService`): camino feliz, fondos
  insuficientes, titularidad, cuentas iguales, cuenta inexistente, idempotencia y conflicto de
  idempotencia.
- **5 escenarios BDD** (Cucumber, en español) de punta a punta por HTTP real: transferencia
  exitosa, fondos insuficientes (422), cuenta ajena (403), sin token (401) e idempotencia.

El reporte HTML de Cucumber se genera en `target/cucumber/report.html`.

## 2. Prueba de humo por HTTP — API corriendo

Salida real de `curl` contra la aplicación levantada (`java -jar ...`):

| # | Caso | HTTP | Respuesta (resumen) |
|---|---|---|---|
| 1 | Balance de origen ANTES | 200 | `balance: 100000.00` |
| 2 | Transferencia (camino feliz) | 201 | `status: POSTED`, `id: 6acc78a0-…` |
| 3 | Balance de origen DESPUÉS | 200 | `balance: 98500.00` (debitó 1500) |
| 4 | Fondos insuficientes | 422 | `code: INSUFFICIENT_FUNDS` |
| 5 | Sin token | 401 | `code: UNAUTHORIZED` |
| 6 | Cuenta ajena (no titular) | 403 | `code: FORBIDDEN` |
| 7a | Idempotencia — 1ª llamada | 201 | `id: 6223bcec-…` |
| 7b | Idempotencia — 2ª llamada (misma key) | 201 | `id: 6223bcec-…` (mismo id, no duplica) |

Cada respuesta de error incluye un `requestId` de correlación (header `X-Request-Id`) para
trazabilidad en logs.

## Cómo regenerar esta evidencia

```bash
# Pruebas
mvn test

# Humo por HTTP
mvn -DskipTests package
java -jar target/nexo-transfer-api-0.1.0.jar &
curl -H "Authorization: Bearer tok-ana-local-dev" localhost:8080/api/v1/accounts/acc-ana-001/balance
# ... (ver docs/runbooks/local-setup.md para la secuencia completa)
```

## Limitaciones de este incremento

- Persistencia **en memoria** (el estado se pierde al reiniciar). La migración a PostgreSQL
  está planificada como incremento posterior (ver `docs/adr/ADR-003`).
- Autenticación por **token estático** (no JWT/OAuth), suficiente para el foco de automatización
  de pruebas; el diseño permite reemplazarla sin tocar los controladores.
