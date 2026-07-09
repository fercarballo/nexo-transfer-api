package com.nexo.transfer.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Cuerpo de la solicitud de creación de una transferencia. Las anotaciones de
 * validación rechazan entradas mal formadas antes de tocar el dominio (fail-fast),
 * devolviendo 400 con un mensaje claro.
 */
public record CreateTransferRequest(
    @NotBlank(message = "sourceAccountId es obligatorio")
    String sourceAccountId,

    @NotBlank(message = "destinationAccountId es obligatorio")
    String destinationAccountId,

    @NotNull(message = "amount es obligatorio")
    @DecimalMin(value = "0.01", message = "amount debe ser mayor a 0")
    BigDecimal amount,

    @NotBlank(message = "currency es obligatorio (ej: ARS)")
    String currency) {
}
