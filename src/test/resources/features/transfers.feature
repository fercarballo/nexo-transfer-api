# language: es
Característica: Transferencias entre cuentas de Nexo Finanzas
  Como titular de una cuenta
  Quiero transferir dinero a otra cuenta
  Para mover mis fondos de forma segura y trazable

  Antecedentes:
    Dado que el usuario "user-ana" está autenticado con el token "tok-ana-local-dev"

  Escenario: Transferencia exitosa entre cuentas
    Cuando transfiere 1000.00 ARS desde "acc-ana-001" hacia "acc-luis-001"
    Entonces la respuesta tiene estado 201
    Y la transferencia queda en estado "POSTED"

  Escenario: Rechazo por fondos insuficientes
    Cuando transfiere 999999.00 ARS desde "acc-ana-002" hacia "acc-luis-001"
    Entonces la respuesta tiene estado 422
    Y el código de error es "INSUFFICIENT_FUNDS"

  Escenario: Rechazo de una transferencia hacia una cuenta ajena por falta de titularidad
    Cuando transfiere 1000.00 ARS desde "acc-luis-001" hacia "acc-ana-001"
    Entonces la respuesta tiene estado 403
    Y el código de error es "FORBIDDEN"

  Escenario: Rechazo sin autenticación
    Dado que el cliente no envía token
    Cuando transfiere 1000.00 ARS desde "acc-ana-001" hacia "acc-luis-001"
    Entonces la respuesta tiene estado 401

  Escenario: La idempotencia evita duplicar una transferencia
    Cuando transfiere 1000.00 ARS desde "acc-ana-001" hacia "acc-luis-001" con Idempotency-Key "bdd-key-1"
    Y transfiere 1000.00 ARS desde "acc-ana-001" hacia "acc-luis-001" con Idempotency-Key "bdd-key-1"
    Entonces ambas respuestas refieren a la misma transferencia
