package com.nexo.transfer.domain;

import java.math.BigDecimal;

/**
 * Orden de creación de una transferencia, ya validada sintácticamente en la capa web.
 * El dominio la recibe con {@code callerUserId} para poder verificar la autorización
 * (que quien transfiere sea dueño de la cuenta de origen) sin depender de detalles HTTP.
 */
public record CreateTransferCommand(
    String callerUserId,
    String sourceAccountId,
    String destinationAccountId,
    BigDecimal amount,
    String currency,
    String idempotencyKey) {
}
