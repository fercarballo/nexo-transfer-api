# Errores frecuentes (y cómo evitarlos)

Errores reales que aparecen al construir o aprender este tipo de sistema. Varios los encontramos y
corregimos en este mismo repositorio.

## 1. Pruebas que dependen del orden (estado compartido)

**Síntoma:** un escenario pasa solo, pero falla cuando corre después de otro.
**Causa:** el store en memoria es compartido por todo el contexto; un escenario deja la cuenta
modificada y "contamina" al siguiente.
**Qué pasó acá:** el escenario de idempotencia fallaba con `INSUFFICIENT_FUNDS` porque una cuenta
venía drenada de escenarios previos.
**Solución:** reiniciar el estado **antes de cada escenario** (`ScenarioHooks` con `@Before`). Datos
deterministas = pruebas independientes.

## 2. Confundir autenticación con autorización

**Autenticación** = *quién sos* (token válido → 401 si falta). **Autorización** = *qué podés hacer*
(ser dueño de la cuenta → 403 si no). Son dos chequeos distintos y ambos necesitan su prueba.

## 3. Idempotencia mal entendida

No alcanza con "si la key existe, devolvé lo anterior". Hay que verificar que el **cuerpo sea el
mismo**: reutilizar una key con otro monto es un error del cliente (`409`), no un duplicado silencioso.

## 4. Devolver el código HTTP incorrecto

Fondos insuficientes **no** es un `400` (la petición está bien formada): es un `422` (no se puede
procesar por una regla de negocio). Elegir bien el código hace las pruebas negativas claras y estables.

## 5. Comparar dinero con `double`

El dinero se maneja con `BigDecimal`, no `double` (que tiene errores de redondeo). Y se compara con
`compareTo`, no con `equals` (que distingue `1000` de `1000.00`).

## 6. Versionar secretos

Nunca subir tokens o credenciales reales. Acá los tokens del `application.yml` son de desarrollo y no
dan acceso a nada; los reales se inyectan por variable de entorno (ver `.env.example`).

## 7. La URL de Postman como objeto solo-`raw`

Al escribir una colección a mano, Newman no resuelve `"url": { "raw": "..." }` sin los componentes
parseados. Usar la URL como **string** (`"url": "{{baseUrl}}/..."`) evita el error `request url is empty`.
