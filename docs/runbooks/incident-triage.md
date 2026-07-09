# Runbook — Triage de incidentes

Guía breve para clasificar y actuar ante un problema reportado en la API. (Contexto de portfolio:
ilustra cómo se pensaría la operación en un entorno real.)

## 1. Clasificar severidad

| Severidad | Ejemplo | Respuesta |
|---|---|---|
| **S1 — crítico** | Débitos duplicados; saldos inconsistentes; caída total | Atención inmediata; frenar el flujo afectado |
| **S2 — alto** | Un endpoint devuelve 500 sistemáticamente | Atención en el día; workaround si existe |
| **S3 — medio** | Un caso de borde da un código de error incorrecto | Planificar corrección |
| **S4 — bajo** | Mensaje de error poco claro | Backlog |

## 2. Recolectar información

- **`X-Request-Id`** del request afectado (viene en la respuesta y en los logs) → correlación.
- Endpoint, cuerpo enviado (sin datos sensibles) y respuesta recibida.
- ¿Reproducible? ¿Desde cuándo? ¿Qué cambió (deploy reciente)?

## 3. Diagnóstico rápido

```bash
# Salud del servicio
curl -s http://localhost:8080/actuator/health
# Buscar el request en los logs por su id de correlación
docker compose logs api | grep "<request-id>"
```

## 4. Hipótesis frecuentes

- **Doble débito** → revisar que el cliente use `Idempotency-Key`; verificar el índice de idempotencia.
- **403 inesperado** → verificar la titularidad de la cuenta vs el token usado.
- **500** → revisar el stack en logs; el `ApiExceptionHandler` sólo devuelve 500 ante errores no previstos.

## 5. Cierre

Todo incidente que revele un hueco de cobertura se convierte en una **prueba de regresión** nueva
(se agrega al dataset de pruebas) para que no vuelva a pasar sin ser detectado.
