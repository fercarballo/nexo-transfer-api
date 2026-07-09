# Glosario

Términos que aparecen en el repositorio, explicados en simple.

- **API REST:** interfaz por HTTP donde cada recurso (cuenta, transferencia) tiene una URL y se opera
  con verbos (GET, POST).
- **Idempotencia:** propiedad por la que repetir una operación produce el mismo efecto que hacerla una
  sola vez. Acá: reintentar una transferencia con la misma `Idempotency-Key` no genera un duplicado.
- **BDD (Behavior-Driven Development):** describir el comportamiento esperado en lenguaje de negocio
  (Dado/Cuando/Entonces) y automatizarlo. Acá se usa Cucumber en español.
- **Cucumber:** herramienta que ejecuta escenarios escritos en Gherkin (Dado/Cuando/Entonces)
  conectándolos a código (step definitions).
- **REST-assured:** librería Java para escribir pruebas de API legibles (given/when/then sobre HTTP).
- **Trazabilidad (correlación):** poder seguir un request de punta a punta. Se logra con un
  identificador único por request (`X-Request-Id`) presente en logs y respuestas.
- **DTO (Data Transfer Object):** objeto que representa el dato que entra o sale por la API, separado
  del modelo interno del dominio.
- **Dominio:** el corazón con las reglas de negocio, independiente de HTTP o base de datos.
- **ADR (Architecture Decision Record):** documento corto que registra una decisión, sus alternativas
  y consecuencias.
- **Quality gate:** control automático que debe pasar para permitir avanzar (ej: que las pruebas
  estén en verde antes de integrar).
- **Seed (siembra de datos):** carga de un estado inicial conocido al arrancar, para tener datos
  deterministas.
- **Actuator:** módulo de Spring Boot que expone endpoints operativos como `/actuator/health`.
- **OpenAPI / Swagger:** estándar para describir una API y UI para explorarla.
- **Bearer token:** credencial que el cliente envía en el header `Authorization: Bearer <token>`.
