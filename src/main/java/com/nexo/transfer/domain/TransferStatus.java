package com.nexo.transfer.domain;

/**
 * Estado de una transferencia. En el MVP una transferencia se procesa de forma
 * síncrona: si pasa todas las reglas queda {@code POSTED}. Se deja explícito el
 * enum (en lugar de un boolean) para habilitar estados futuros (PENDING, REVERSED)
 * sin romper el contrato.
 */
public enum TransferStatus {
  POSTED
}
