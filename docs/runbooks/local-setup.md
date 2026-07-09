# Runbook — Setup local

## Prerequisitos

| Herramienta | Versión | Para qué |
|---|---|---|
| JDK | 21 | Compilar y correr la app |
| Maven | 3.9+ | Build y pruebas |
| Docker (opcional) | reciente | Correr en contenedor |
| Newman (opcional) | 6+ | Ejecutar la colección Postman |

En macOS con Homebrew:

```bash
brew install openjdk@21 maven
npm install -g newman     # opcional
```

Si `java -version` no encuentra el JDK, exportá `JAVA_HOME`:

```bash
export JAVA_HOME="$(brew --prefix openjdk@21)/libexec/openjdk.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
```

## Correr las pruebas

```bash
mvn test        # 12 pruebas: 7 unitarias + 5 escenarios BDD
```

## Correr la app

```bash
# Local
mvn -DskipTests package
java -jar target/nexo-transfer-api-0.1.0.jar
# → http://localhost:8080  (Swagger UI en /swagger-ui.html)

# Contenedor
docker compose up --build
```

## Probar manualmente

```bash
TOK="Authorization: Bearer tok-ana-local-dev"

# Saldo
curl -H "$TOK" http://localhost:8080/api/v1/accounts/acc-ana-001/balance

# Transferencia
curl -X POST -H "$TOK" -H 'Content-Type: application/json' \
  -d '{"sourceAccountId":"acc-ana-001","destinationAccountId":"acc-luis-001","amount":1000.00,"currency":"ARS"}' \
  http://localhost:8080/api/v1/transfers
```

## Cuentas y tokens de demostración

| Token (local) | Usuario | Cuentas |
|---|---|---|
| `tok-ana-local-dev` | user-ana | `acc-ana-001` (100000), `acc-ana-002` (5000) |
| `tok-luis-local-dev` | user-luis | `acc-luis-001` (25000) |

> Son valores de desarrollo, no secretos. En un entorno real se inyectan por variables de entorno.
