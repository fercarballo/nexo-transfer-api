package com.nexo.transfer.api;

import com.nexo.transfer.domain.Transfer;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Representación de una transferencia expuesta por la API. Se separa del modelo de
 * dominio ({@link Transfer}) para poder evolucionar el contrato público de forma
 * independiente de la implementación interna.
 */
public record TransferView(
    String id,
    String sourceAccountId,
    String destinationAccountId,
    BigDecimal amount,
    String currency,
    String status,
    Instant createdAt) {

  public static TransferView from(Transfer t) {
    return new TransferView(
        t.id(),
        t.sourceAccountId(),
        t.destinationAccountId(),
        t.amount(),
        t.currency(),
        t.status().name(),
        t.createdAt());
  }
}
