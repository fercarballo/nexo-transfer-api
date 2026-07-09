# Matriz de riesgos

Riesgos de negocio y técnicos, con impacto × probabilidad y su mitigación. Escala: Alto / Medio / Bajo.

## Riesgos de negocio

| ID | Riesgo | Impacto | Prob. | Mitigación | Verificación |
|---|---|---|---|---|---|
| RB1 | **Doble débito** por un reintento de red | Alto | Media | Idempotencia con `Idempotency-Key` + detección de conflicto | Pruebas de idempotencia (unit + BDD + Postman) |
| RB2 | **Acceso a cuentas ajenas** | Alto | Media | Autorización por titularidad del origen; lectura solo si el usuario está involucrado | Escenarios 403 (BDD) y `AccountController` |
| RB3 | **Saldo negativo** por transferir sin fondos | Alto | Media | Chequeo de fondos antes de debitar; débito/crédito bajo lock | `insufficientFunds` (unit) + escenario 422 |
| RB4 | **Inconsistencia** de saldos por concurrencia | Alto | Baja | Sección crítica sincronizada en `TransferService` | Reglas cubiertas; carga real se evalúa en `nexo-performance-lab` |

## Riesgos técnicos

| ID | Riesgo | Impacto | Prob. | Mitigación | Verificación |
|---|---|---|---|---|---|
| RT1 | **Fuga de secretos** en el repo | Alto | Baja | Sin secretos versionados; `.env.example`; tokens locales no-secretos; escaneo en CI | Barrido de seguridad + revisión de PR |
| RT2 | **Entorno no reproducible** | Medio | Media | Docker Compose + versiones fijas (JDK 21, Boot 3.3) | `docker compose up` documentado y verificado |
| RT3 | **Pruebas flaky** por estado compartido | Medio | Media | Reinicio de estado por escenario (`ScenarioHooks`) | Corridas repetidas en verde |
| RT4 | **Sobre-alcance** del MVP | Medio | Alta | Alcance acotado + backlog de mejoras explícito | Revisión de alcance por incremento |
| RT5 | **Pérdida de datos** (store en memoria) | Bajo* | Alta | Aceptado en MVP; migración a PostgreSQL planificada (ADR-003) | Límite documentado |

\* Bajo por tratarse de un entorno de demostración con datos ficticios.

## Lectura

Los riesgos Alto×Media (RB1, RB2, RB3) son los que más pruebas concentran: son el corazón de un
sistema de transferencias. RT4 (sobre-alcance) se gestiona con la disciplina de incrementos.
