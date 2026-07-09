# Runbook — Resolución de problemas

| Síntoma | Causa probable | Solución |
|---|---|---|
| `Unable to locate a Java Runtime` | No hay JDK o `JAVA_HOME` sin setear | Instalar JDK 21 y exportar `JAVA_HOME` (ver local-setup.md). |
| `mvn` usa una versión de Java incorrecta | `JAVA_HOME` apunta a otro JDK | `export JAVA_HOME=$(brew --prefix openjdk@21)/libexec/openjdk.jdk/Contents/Home`. |
| Puerto 8080 ocupado | Otra app usa el puerto | Correr en otro puerto: `--server.port=8085` o `PORT=8085`. |
| `401 UNAUTHORIZED` en todas las llamadas | Falta el header o token inválido | Enviar `Authorization: Bearer tok-ana-local-dev`. |
| `403 FORBIDDEN` al transferir | La cuenta de origen no es del usuario del token | Usar una cuenta propia del usuario (ver tabla en local-setup.md). |
| `422 INSUFFICIENT_FUNDS` inesperado | La app viene de una corrida previa con estado modificado | Reiniciar la app (el store es en memoria; el seed vuelve al estado inicial). |
| Pruebas BDD flaky entre corridas | Estado compartido entre escenarios | Ya mitigado con `ScenarioHooks` (reinicio por escenario). Si reaparece, revisar ese hook. |
| `docker compose` no conecta al daemon | Docker/colima detenido | `colima start` (o abrir Docker Desktop). |
| El healthcheck del contenedor queda en `starting` | La app aún no terminó de arrancar | Esperar; `start_period` es 20s. Ver logs: `docker compose logs api`. |

## Ver logs con el id de correlación

Cada request tiene un `X-Request-Id` (en el header de respuesta y en los logs). Para rastrear un
error puntual, buscá ese id en la salida de la aplicación.
