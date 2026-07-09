# ADR-002 — Autenticación por bearer token estático (MVP)

- **Estado:** Aceptada (revisable)
- **Fecha:** 2026-07-09

## Contexto

La API necesita **autenticar** (quién sos) y **autorizar** (qué podés tocar: solo tus cuentas). El
foco del portfolio es la **automatización de pruebas**, no construir un proveedor de identidad.

## Decisión

Un **bearer token estático** mapeado a un `userId`, validado por un filtro (`TokenAuthFilter`) que
publica el `userId` como atributo del request. La autorización por titularidad se resuelve en la capa
de dominio/controlador.

Los tokens de desarrollo viven en `application.yml` (valores **no secretos**, solo locales) y en un
entorno real se inyectan por variable de entorno.

## Alternativas consideradas

| Alternativa | Por qué se descartó (por ahora) |
|---|---|
| **Spring Security + JWT** | Más realista, pero agrega complejidad que no aporta al foco (testing). El diseño por atributos permite migrar a esto **sin tocar los controladores**. |
| **OAuth2 / OIDC con proveedor externo** | Requiere infraestructura/cuenta externa; fuera del alcance de un MVP local y reproducible. |
| **Sin auth** | Inaceptable: la autorización por titularidad es una regla de negocio central que hay que poder probar. |

## Consecuencias

- (+) Simple, local, reproducible y suficiente para ejercitar autenticación (401) y autorización (403).
- (+) Sustituible por Spring Security real sin reescribir controladores.
- (−) No es apto para producción como está (sin expiración, rotación ni firma). Documentado como límite.

## Riesgo que cubre / acepta

Cubre el riesgo de **acceso a cuentas ajenas** (R2) con pruebas negativas. Acepta el riesgo de un
esquema de auth simplificado, acotado a un entorno de demostración.

## Cómo se verifica

Escenarios BDD "sin token" (401) y "cuenta ajena" (403); ver `traceability.md`.
