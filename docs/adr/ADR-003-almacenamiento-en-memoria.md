# ADR-003 — Almacenamiento en memoria (MVP), con migración planificada a PostgreSQL

- **Estado:** Aceptada (transitoria)
- **Fecha:** 2026-07-09

## Contexto

El MVP necesita persistir cuentas y transferencias. La prioridad es un incremento **vertical y
verificable** rápido, sin arrastrar infraestructura antes de tiempo.

## Decisión

Persistir en **memoria** (`ConcurrentHashMap`) detrás de una **interfaz de repositorio**
(`AccountRepository`, `TransferRepository`). El estado inicial se siembra al arrancar (`DataSeeder`).

## Alternativas consideradas

| Alternativa | Por qué se descartó (por ahora) |
|---|---|
| **PostgreSQL desde el día 1** | Es el destino, pero agrega infra (contenedor, migraciones, mapeo) que retrasa el primer incremento verificable. Se incorporará cuando el dominio esté estable. |
| **H2 embebida** | Válida como paso intermedio; la elegiríamos si necesitáramos SQL real en pruebas. Para el MVP, el mapa en memoria es aún más simple y suficiente. |

## Consecuencias

- (+) Cero infraestructura; pruebas rapidísimas; foco en el dominio.
- (−) El estado **no persiste** entre reinicios y no valida SQL real. Documentado como límite.
- (Diseño) Al estar detrás de una interfaz, migrar a PostgreSQL **no toca** el dominio ni la web:
  se implementa la interfaz con un repositorio JDBC/JPA y se conecta por Docker Compose.

## Nota de aislamiento en pruebas

Como el store es compartido por el contexto de Spring, las pruebas BDD lo **reinician antes de cada
escenario** (`ScenarioHooks`), garantizando datos deterministas e independencia del orden.

## Cómo se verifica

`TransferServiceTest` ejercita las reglas contra el repositorio en memoria; los escenarios BDD
validan el comportamiento de punta a punta con estado reiniciado por escenario.
