# Cómo contribuir

Gracias por tu interés. Esta guía resume el flujo de trabajo y los estándares del repositorio.

## Flujo

1. Creá una rama desde `main`: `git checkout -b feat/mi-cambio`.
2. Trabajá en **incrementos verticales**: funcionalidad + prueba + documentación + evidencia.
3. Verificá localmente antes de abrir el PR (ver más abajo).
4. Abrí un Pull Request usando la plantilla; enlazá el issue o requisito que resuelve.

## Estándar de commits

Usamos mensajes semánticos y en incrementos pequeños:

```
feat: soporta idempotencia en la creación de transferencias
fix: corrige el cálculo de saldo tras un rechazo
docs: agrega ADR sobre la elección de Spring Boot
test: cubre el conflicto de idempotencia
```

## Verificación local (obligatoria antes del PR)

```bash
mvn test        # unitarias + BDD (deben quedar en verde)
mvn -DskipTests package && docker compose up --build   # smoke opcional
```

## Definición de Hecho (resumen)

Un cambio está "hecho" cuando: pasa las pruebas, tiene pruebas nuevas si agregó comportamiento,
actualiza la documentación afectada, no versiona secretos y deja evidencia reproducible cuando
corresponde. La versión completa está en
[`docs/quality/definition-of-done.md`](docs/quality/definition-of-done.md).

## Reglas de oro

- No inventar resultados de pruebas ni de integraciones. Si algo no se ejecutó, se dice.
- No versionar secretos, tokens reales ni PII. Todo por variables de entorno.
- Preferir soluciones simples, mantenibles y ejecutables localmente.
