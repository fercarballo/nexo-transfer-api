package com.nexo.transfer.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Reglas de negocio de una transferencia. Es el corazón del sistema y no depende
 * de HTTP: recibe un {@link CreateTransferCommand} y decide. Esto la hace testeable
 * de forma unitaria (sin levantar el servidor) además de por BDD de punta a punta.
 *
 * <p>Orden de las validaciones (de autorización a negocio):
 * <ol>
 *   <li>Idempotencia: si la clave ya existe, se resuelve el reintento.</li>
 *   <li>Existencia y titularidad de la cuenta de origen (autorización).</li>
 *   <li>Existencia de la cuenta de destino.</li>
 *   <li>Reglas: cuentas distintas, monedas coincidentes, saldo suficiente.</li>
 *   <li>Ejecución atómica del débito/crédito.</li>
 * </ol>
 */
@Service
public class TransferService {

  private final AccountRepository accounts;
  private final TransferRepository transfers;
  private final Object executionLock = new Object();

  public TransferService(AccountRepository accounts, TransferRepository transfers) {
    this.accounts = accounts;
    this.transfers = transfers;
  }

  public Transfer createTransfer(CreateTransferCommand cmd) {
    // 1) Idempotencia: un reintento con la misma clave no debe duplicar la operación.
    if (cmd.idempotencyKey() != null && !cmd.idempotencyKey().isBlank()) {
      var existing = transfers.findByIdempotencyKey(cmd.idempotencyKey());
      if (existing.isPresent()) {
        assertSameRequest(existing.get(), cmd);
        return existing.get();
      }
    }

    synchronized (executionLock) {
      // Re-chequeo dentro del lock por si dos reintentos llegan en paralelo.
      if (cmd.idempotencyKey() != null && !cmd.idempotencyKey().isBlank()) {
        var existing = transfers.findByIdempotencyKey(cmd.idempotencyKey());
        if (existing.isPresent()) {
          assertSameRequest(existing.get(), cmd);
          return existing.get();
        }
      }

      // 2) Origen: existe y pertenece al usuario autenticado.
      Account source = accounts.findById(cmd.sourceAccountId())
          .orElseThrow(() -> new TransferExceptions.AccountNotFound(cmd.sourceAccountId()));
      if (!source.ownerId().equals(cmd.callerUserId())) {
        throw new TransferExceptions.Forbidden(
            "La cuenta de origen no pertenece al usuario autenticado.");
      }

      // 3) Destino: existe.
      Account destination = accounts.findById(cmd.destinationAccountId())
          .orElseThrow(() -> new TransferExceptions.AccountNotFound(cmd.destinationAccountId()));

      // 4) Reglas de negocio.
      if (source.id().equals(destination.id())) {
        throw new TransferExceptions.Invalid("Origen y destino no pueden ser la misma cuenta.");
      }
      if (!source.currency().equals(cmd.currency()) || !destination.currency().equals(cmd.currency())) {
        throw new TransferExceptions.Invalid(
            "La moneda de la transferencia no coincide con la de las cuentas.");
      }
      if (!source.hasSufficientFunds(cmd.amount())) {
        throw new TransferExceptions.InsufficientFunds(source.id());
      }

      // 5) Ejecución.
      source.debit(cmd.amount());
      destination.credit(cmd.amount());
      accounts.save(source);
      accounts.save(destination);

      Transfer transfer = new Transfer(
          UUID.randomUUID().toString(),
          source.id(),
          destination.id(),
          cmd.amount(),
          cmd.currency(),
          TransferStatus.POSTED,
          cmd.idempotencyKey(),
          Instant.now());
      return transfers.save(transfer);
    }
  }

  /**
   * Garantiza que reutilizar una Idempotency-Key sea con la misma solicitud.
   * Reintentar con la misma clave pero distinto cuerpo es un error del cliente (409),
   * no un duplicado silencioso.
   */
  private void assertSameRequest(Transfer original, CreateTransferCommand cmd) {
    boolean same = original.sourceAccountId().equals(cmd.sourceAccountId())
        && original.destinationAccountId().equals(cmd.destinationAccountId())
        && original.currency().equals(cmd.currency())
        && original.amount().compareTo(cmd.amount()) == 0;
    if (!same) {
      throw new TransferExceptions.IdempotencyConflict(cmd.idempotencyKey());
    }
  }
}
