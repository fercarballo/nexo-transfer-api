# Evidencia — Incremento 2: colección Postman/Newman y contenedor Docker

Fecha: 2026-07-09 · Entorno: local (Newman 6.2.2, Docker vía colima)

## 1. Colección Postman ejecutada con Newman

Salida real de `newman run postman/nexo-transfer-api.postman_collection.json`:

```
┌─────────────────────────┬──────────┬──────────┐
│                         │ executed │   failed │
├─────────────────────────┼──────────┼──────────┤
│              iterations │        1 │        0 │
│                requests │        6 │        0 │
│            test-scripts │        6 │        0 │
│      prerequest-scripts │        1 │        0 │
│              assertions │       10 │        0 │
└─────────────────────────┴──────────┴──────────┘
average response time: 11ms
```

**10/10 aserciones en verde**: balance, camino feliz (201/POSTED), fondos insuficientes
(422/INSUFFICIENT_FUNDS), sin autenticación (401) e idempotencia (misma key → mismo id).
El reporte completo queda en `evidence/newman-report.json`.

## 2. Contenedor Docker (build + run + healthcheck)

Salida real de `docker compose build` + `up`:

```
BUILD OK
health=starting (1)
health=starting (2)
health=healthy   (3)

health:   {"status":"UP"}
transfer: 201 {"id":"8d699fa9-…","status":"POSTED","amount":250.00,"currency":"ARS", …}

nexo-transfer-api:0.1.0   533MB
```

- Imagen **multi-stage** (build con Maven+JDK 21, runtime con JRE 21 + usuario no-root).
- **Healthcheck** de Docker en verde (`/actuator/health` → UP).
- Transferencia real `201` contra el contenedor.

## Cómo regenerar

```bash
# Newman (requiere la app corriendo en :8080)
newman run postman/nexo-transfer-api.postman_collection.json

# Docker
colima start                 # o Docker Desktop
docker compose build
docker compose up -d
curl localhost:8080/actuator/health
docker compose down
```
