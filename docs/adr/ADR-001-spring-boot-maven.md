# ADR-001 — Spring Boot + Maven para la API

- **Estado:** Aceptada
- **Fecha:** 2026-07-09

## Contexto

Necesitamos construir una API de dominio financiero que sea **creíble para el sector** (el objetivo
del portfolio es una búsqueda de Automation Tester Sr en banca), **testeable** en varias capas y
**reproducible** localmente.

## Decisión

Usar **Java 21 + Spring Boot 3 + Maven**.

- Spring Boot: estándar de facto en aplicaciones empresariales/financieras en la JVM. Trae
  validación, manejo de errores, OpenAPI (springdoc), Actuator (health/trazabilidad) y un modelo de
  pruebas de primera clase (`@SpringBootTest`, puerto aleatorio) que habilita BDD de punta a punta.
- Maven: build declarativo y ubicuo en el ecosistema Java empresarial.

## Alternativas consideradas

| Alternativa | Por qué se descartó |
|---|---|
| **Javalin / Spark (micro-frameworks JVM)** | Más livianos y rápidos de levantar, pero poco representativos de lo que se usa en un banco; menos "batteries included" (OpenAPI, testing). |
| **Quarkus / Micronaut** | Modernos y válidos, pero Spring es la señal más segura para el rol objetivo y tiene la mayor base de conocimiento. |
| **Node/Express, Python/FastAPI** | Contradicen el perfil: el rol pide **Java** explícitamente. |
| **Gradle en lugar de Maven** | Igualmente válido; Maven es más prevalente en banca y su POM declarativo es más universal de leer. |

## Consecuencias

- (+) Máxima credibilidad y empleabilidad para el objetivo; testing potente incluido.
- (+) OpenAPI, health y validación "gratis".
- (−) Arranque algo más pesado que un micro-framework; se mitiga con Docker y con pruebas unitarias
  de dominio que no levantan el contexto.

## Cómo se verifica

`mvn test` compila y ejecuta las 12 pruebas; `mvn -DskipTests package` produce un jar ejecutable.
