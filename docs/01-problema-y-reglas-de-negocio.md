# Problema y reglas de negocio

## Qué problema resuelve

Mover dinero entre cuentas es una operación **crítica**: un error puede duplicar un débito, dejar
una cuenta en rojo o exponer datos de un tercero. `nexo-transfer-api` modela esa operación con las
garantías que se esperan en un sistema financiero, en un alcance acotado y **ficticio** apto para
un portfolio.

El valor para el resto del ecosistema: esta API es el **contrato** que los canales web y mobile
consumen. Definir bien su comportamiento y sus errores permite automatizar pruebas confiables en
todas las capas.

## Conceptos del dominio

- **Cuenta (Account):** tiene un `id`, un **dueño** (`ownerId`), una **moneda** y un **saldo**.
- **Transferencia (Transfer):** mueve un **monto** de una cuenta origen a una destino. Una vez
  procesada queda **POSTED** (contabilizada) y es inmutable.
- **Usuario:** se identifica por un bearer token. Un usuario solo puede operar sobre **sus** cuentas.

## Reglas de negocio

| # | Regla | Resultado si se viola |
|---|---|---|
| R1 | El origen debe existir | `404 ACCOUNT_NOT_FOUND` |
| R2 | El origen debe pertenecer al usuario autenticado | `403 FORBIDDEN` |
| R3 | El destino debe existir | `404 ACCOUNT_NOT_FOUND` |
| R4 | Origen y destino deben ser distintos | `400 INVALID_REQUEST` |
| R5 | La moneda debe coincidir (origen, destino y solicitud) | `400 INVALID_REQUEST` |
| R6 | El monto debe ser > 0 | `400 VALIDATION_ERROR` |
| R7 | El origen debe tener saldo suficiente | `422 INSUFFICIENT_FUNDS` |
| R8 | Idempotencia: misma key + mismo cuerpo → no duplica | Devuelve la transferencia original |
| R9 | Idempotencia: misma key + cuerpo distinto → conflicto | `409 IDEMPOTENCY_CONFLICT` |

## Por qué idempotencia (regla clave)

En una red real, un cliente puede reintentar una solicitud (timeout, reintento automático) sin saber
si la primera llegó. Sin idempotencia, ese reintento **duplicaría** la transferencia. Con la
`Idempotency-Key`, el segundo intento devuelve el resultado del primero: **exactamente una** ejecución.
Es un requisito estándar en pagos y transferencias.

## Cómo verificar estas reglas

Cada regla tiene su prueba. La correspondencia requisito→prueba está en
[quality/traceability.md](quality/traceability.md). Para correrlas: `mvn test`.

## Fuera de alcance (MVP)

Persistencia real, múltiples monedas con conversión, reversos/anulaciones, límites diarios,
comisiones y notificaciones. Están en el backlog de mejoras posteriores.
