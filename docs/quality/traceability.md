# Trazabilidad requisito → prueba

Cada regla de negocio se enlaza con la prueba que la verifica. Esto permite responder "¿esto está
probado?" y detectar huecos de cobertura.

| Regla | Descripción | Prueba unitaria | Escenario BDD | Postman |
|---|---|---|---|---|
| R1 | Origen inexistente → 404 | `rejectsUnknownAccount` | — | — |
| R2 | Origen ajeno → 403 | `forbiddenWhenNotOwner` | "cuenta ajena por falta de titularidad" | — |
| R4 | Origen = destino → 400 | `rejectsSameAccount` | — | — |
| R6 | Monto ≤ 0 → 400 | (validación DTO) | — | — |
| R7 | Fondos insuficientes → 422 | `insufficientFunds` | "rechazo por fondos insuficientes" | "Rechazo por fondos insuficientes" |
| R8 | Idempotencia: no duplica | `idempotentRetry` | "la idempotencia evita duplicar" | "Idempotencia — reintento" |
| R9 | Idempotencia: conflicto → 409 | `idempotencyConflict` | — | — |
| Auth | Sin token → 401 | — | "rechazo sin autenticación" | "Rechazo sin autenticación" |
| Feliz | Transferencia exitosa → 201 / POSTED | `happyPath` | "transferencia exitosa" | "Crear transferencia (camino feliz)" |

## Huecos conocidos (backlog de pruebas)

- R3 (destino inexistente) y R5 (moneda distinta) están **implementados** y cubiertos indirectamente;
  falta un caso dedicado explícito → pendiente en el próximo incremento.
- Concurrencia (RB4): cubierta por diseño (lock); la validación bajo carga real corresponde a
  `nexo-performance-lab`.

## Cómo se mantiene

Cuando se agrega o cambia una regla, se actualiza esta tabla en el mismo PR (ver Definición de Hecho).
