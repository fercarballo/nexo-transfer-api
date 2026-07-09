# Ejercicios guiados

Ejercicios para aprender el código modificándolo. Después de cada uno, corré `mvn test`.

## 1. (Fácil) Agregá una regla: monto máximo por transferencia

**Objetivo:** rechazar transferencias por encima de un límite (ej: 1.000.000).

1. En `TransferService`, agregá la validación antes de chequear fondos.
2. Lanzá `TransferExceptions.Invalid` con un mensaje claro.
3. Escribí una prueba unitaria en `TransferServiceTest` que espere el rechazo.
4. Agregá un escenario BDD en `transfers.feature`.

**Pista:** mirá cómo se valida "origen = destino" y seguí el mismo patrón.

## 2. (Media) Nuevo caso negativo: moneda distinta

**Objetivo:** cubrir la regla R5 (moneda no coincide) con una prueba dedicada.

1. Agregá una cuenta en otra moneda al `DataSeeder` (ej: USD).
2. Escribí una prueba que intente transferir entre monedas distintas y espere `400 INVALID_REQUEST`.
3. Actualizá `traceability.md` marcando R5 como cubierta.

## 3. (Media) Endpoint de listado de transferencias

**Objetivo:** `GET /api/v1/transfers` que devuelva las transferencias del usuario autenticado.

1. Agregá un método en `TransferRepository` para listar por cuenta.
2. Creá el endpoint en `TransferController`, filtrando por titularidad.
3. Cubrilo con una prueba BDD.

## 4. (Avanzada) Persistencia con PostgreSQL

**Objetivo:** implementar ADR-003.

1. Agregá una implementación JPA/JDBC de los repositorios.
2. Agregá un servicio Postgres al `docker-compose.yml`.
3. Corré las mismas pruebas contra la base real (idealmente con Testcontainers).

## Cómo saber si lo hiciste bien

`mvn test` en verde y, si agregaste comportamiento, pruebas nuevas que lo cubren. Si tocaste una
regla, `traceability.md` actualizado.
