# Empezar aquí

Esta guía es el punto de entrada para alguien que llega por primera vez al repositorio.

## En qué orden leer

1. **[README.md](../README.md)** — qué es, cómo arrancar en 5 minutos.
2. **[01-problema-y-reglas-de-negocio.md](01-problema-y-reglas-de-negocio.md)** — qué problema
   resuelve y las reglas del dominio.
3. **[architecture/context.md](architecture/context.md)** y
   **[architecture/containers.md](architecture/containers.md)** — cómo está organizado.
4. **[adr/](adr/)** — por qué se tomó cada decisión importante.
5. **[quality/test-strategy.md](quality/test-strategy.md)** — cómo se prueba y por qué.

## Levantar el proyecto en 3 pasos

```bash
# 1. Prerequisitos: JDK 21 y Maven (o solo Docker). Ver runbooks/local-setup.md.
# 2. Pruebas
mvn test
# 3. Correr
mvn -DskipTests package && java -jar target/nexo-transfer-api-0.1.0.jar
```

Si algo falla, ver **[runbooks/troubleshoot.md](runbooks/troubleshoot.md)**.

## Glosario rápido

¿Un término no te suena (idempotencia, BDD, trazabilidad)? Está en
**[learning/glossary.md](learning/glossary.md)**.

## Cómo está pensada esta documentación

Cada documento intenta responder, cuando aplica: **qué** problema resuelve, **cómo** funciona,
**por qué** se decidió así, **qué alternativas** se evaluaron, **qué riesgo** cubre, **cómo
verificarlo** y **qué errores frecuentes** aparecen al aprender. Es documentación para *entender y
reutilizar*, no solo para operar.
