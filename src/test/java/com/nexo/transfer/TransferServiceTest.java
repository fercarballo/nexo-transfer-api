package com.nexo.transfer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.nexo.transfer.domain.Account;
import com.nexo.transfer.domain.AccountRepository;
import com.nexo.transfer.domain.CreateTransferCommand;
import com.nexo.transfer.domain.Transfer;
import com.nexo.transfer.domain.TransferExceptions;
import com.nexo.transfer.domain.TransferRepository;
import com.nexo.transfer.domain.TransferService;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias de las reglas de negocio de {@link TransferService}. No levantan
 * el servidor: ejercitan el dominio de forma directa y rápida. Cubren el camino feliz
 * y cada regla de rechazo, incluida la idempotencia.
 */
class TransferServiceTest {

  private AccountRepository accounts;
  private TransferRepository transfers;
  private TransferService service;

  @BeforeEach
  void setUp() {
    accounts = new AccountRepository();
    transfers = new TransferRepository();
    service = new TransferService(accounts, transfers);
    accounts.save(new Account("acc-ana-001", "user-ana", "ARS", new BigDecimal("100000.00")));
    accounts.save(new Account("acc-ana-002", "user-ana", "ARS", new BigDecimal("5000.00")));
    accounts.save(new Account("acc-luis-001", "user-luis", "ARS", new BigDecimal("25000.00")));
  }

  private CreateTransferCommand command(String caller, String from, String to, String amount,
      String idempotencyKey) {
    return new CreateTransferCommand(caller, from, to, new BigDecimal(amount), "ARS", idempotencyKey);
  }

  @Test
  @DisplayName("Camino feliz: debita origen, acredita destino y postea la transferencia")
  void happyPath() {
    Transfer transfer = service.createTransfer(
        command("user-ana", "acc-ana-001", "acc-luis-001", "1000.00", null));

    assertThat(transfer.status().name()).isEqualTo("POSTED");
    assertThat(accounts.findById("acc-ana-001").orElseThrow().balance())
        .isEqualByComparingTo("99000.00");
    assertThat(accounts.findById("acc-luis-001").orElseThrow().balance())
        .isEqualByComparingTo("26000.00");
  }

  @Test
  @DisplayName("Rechaza por fondos insuficientes y no modifica saldos")
  void insufficientFunds() {
    assertThatThrownBy(() -> service.createTransfer(
        command("user-ana", "acc-ana-002", "acc-luis-001", "999999.00", null)))
        .isInstanceOf(TransferExceptions.InsufficientFunds.class);

    assertThat(accounts.findById("acc-ana-002").orElseThrow().balance())
        .isEqualByComparingTo("5000.00");
  }

  @Test
  @DisplayName("Rechaza si el origen no pertenece al usuario autenticado")
  void forbiddenWhenNotOwner() {
    assertThatThrownBy(() -> service.createTransfer(
        command("user-luis", "acc-ana-001", "acc-luis-001", "1000.00", null)))
        .isInstanceOf(TransferExceptions.Forbidden.class);
  }

  @Test
  @DisplayName("Rechaza origen y destino iguales")
  void rejectsSameAccount() {
    assertThatThrownBy(() -> service.createTransfer(
        command("user-ana", "acc-ana-001", "acc-ana-001", "1000.00", null)))
        .isInstanceOf(TransferExceptions.Invalid.class);
  }

  @Test
  @DisplayName("Rechaza cuenta de origen inexistente")
  void rejectsUnknownAccount() {
    assertThatThrownBy(() -> service.createTransfer(
        command("user-ana", "acc-inexistente", "acc-luis-001", "1000.00", null)))
        .isInstanceOf(TransferExceptions.AccountNotFound.class);
  }

  @Test
  @DisplayName("Idempotencia: reintento con la misma clave no duplica el débito")
  void idempotentRetry() {
    Transfer first = service.createTransfer(
        command("user-ana", "acc-ana-001", "acc-luis-001", "1000.00", "key-123"));
    Transfer second = service.createTransfer(
        command("user-ana", "acc-ana-001", "acc-luis-001", "1000.00", "key-123"));

    assertThat(second.id()).isEqualTo(first.id());
    // Un solo débito, aunque se llamó dos veces con la misma clave.
    assertThat(accounts.findById("acc-ana-001").orElseThrow().balance())
        .isEqualByComparingTo("99000.00");
  }

  @Test
  @DisplayName("Idempotencia: misma clave con distinto cuerpo es conflicto 409")
  void idempotencyConflict() {
    service.createTransfer(command("user-ana", "acc-ana-001", "acc-luis-001", "1000.00", "key-x"));
    assertThatThrownBy(() -> service.createTransfer(
        command("user-ana", "acc-ana-001", "acc-luis-001", "2000.00", "key-x")))
        .isInstanceOf(TransferExceptions.IdempotencyConflict.class);
  }
}
