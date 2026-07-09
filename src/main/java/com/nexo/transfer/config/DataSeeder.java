package com.nexo.transfer.config;

import com.nexo.transfer.domain.Account;
import com.nexo.transfer.domain.AccountRepository;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Carga cuentas ficticias al arrancar. Son datos de demostración, sin relación con
 * ninguna entidad real. Al ser un store en memoria, este seed define el estado inicial
 * conocido sobre el que corren las pruebas (datos deterministas = pruebas estables).
 *
 * <p>Titularidad: las cuentas ana-* pertenecen a {@code user-ana} y luis-* a {@code user-luis},
 * lo que permite ejercitar la autorización por dueño.
 */
@Component
public class DataSeeder implements CommandLineRunner {

  private final AccountRepository accounts;

  public DataSeeder(AccountRepository accounts) {
    this.accounts = accounts;
  }

  @Override
  public void run(String... args) {
    seed();
  }

  /**
   * Restablece las cuentas ficticias a su estado inicial conocido. Es idempotente:
   * sobreescribe siempre los mismos ids, por lo que puede invocarse para reiniciar el
   * estado entre pruebas sin acumular datos.
   */
  public void seed() {
    accounts.save(new Account("acc-ana-001", "user-ana", "ARS", new BigDecimal("100000.00")));
    accounts.save(new Account("acc-ana-002", "user-ana", "ARS", new BigDecimal("5000.00")));
    accounts.save(new Account("acc-luis-001", "user-luis", "ARS", new BigDecimal("25000.00")));
  }
}
