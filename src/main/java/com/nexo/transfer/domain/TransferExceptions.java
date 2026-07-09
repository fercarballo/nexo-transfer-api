package com.nexo.transfer.domain;

/**
 * Excepciones de dominio agrupadas. Cada una representa una regla de negocio
 * violada y se traduce a un código HTTP concreto en el manejador de la capa web
 * (ver {@code ApiExceptionHandler}). Agruparlas evita dispersar archivos triviales
 * y deja el catálogo de errores del dominio en un solo lugar legible.
 */
public final class TransferExceptions {

  private TransferExceptions() {
  }

  /** La cuenta indicada no existe. → HTTP 404 */
  public static class AccountNotFound extends RuntimeException {
    public AccountNotFound(String accountId) {
      super("La cuenta no existe: " + accountId);
    }
  }

  /** El recurso solicitado no existe (ej: transferencia). → HTTP 404 */
  public static class NotFound extends RuntimeException {
    public NotFound(String message) {
      super(message);
    }
  }

  /** La cuenta de origen no tiene saldo suficiente. → HTTP 422 */
  public static class InsufficientFunds extends RuntimeException {
    public InsufficientFunds(String accountId) {
      super("Fondos insuficientes en la cuenta: " + accountId);
    }
  }

  /** El usuario autenticado no es dueño del recurso. → HTTP 403 */
  public static class Forbidden extends RuntimeException {
    public Forbidden(String message) {
      super(message);
    }
  }

  /** Regla de negocio invalidada (misma cuenta, moneda distinta, etc.). → HTTP 400 */
  public static class Invalid extends RuntimeException {
    public Invalid(String message) {
      super(message);
    }
  }

  /**
   * Se reutilizó una Idempotency-Key con un cuerpo distinto al original. → HTTP 409.
   * Reutilizarla con el mismo cuerpo NO es un error: devuelve la transferencia original.
   */
  public static class IdempotencyConflict extends RuntimeException {
    public IdempotencyConflict(String key) {
      super("La Idempotency-Key ya se usó con una solicitud diferente: " + key);
    }
  }
}
