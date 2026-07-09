package com.nexo.transfer.domain;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Transferencia ya procesada. Es inmutable (record): una vez posteada, no cambia.
 *
 * @param idempotencyKey clave opcional de idempotencia con la que se creó; permite
 *                       que un reintento del cliente devuelva esta misma transferencia
 *                       en lugar de generar un duplicado.
 */
public record Transfer(
    String id,
    String sourceAccountId,
    String destinationAccountId,
    BigDecimal amount,
    String currency,
    TransferStatus status,
    String idempotencyKey,
    Instant createdAt) {
}
