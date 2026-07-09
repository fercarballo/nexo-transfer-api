package com.nexo.transfer.domain;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de cuentas en memoria (MVP). Se aísla detrás de esta clase para que
 * migrar a una base real (Postgres, ver ADR-003) no toque el servicio de dominio.
 * Usa {@link ConcurrentHashMap} para ser seguro ante requests concurrentes.
 */
@Repository
public class AccountRepository {

  private final ConcurrentHashMap<String, Account> accounts = new ConcurrentHashMap<>();

  public Optional<Account> findById(String id) {
    return Optional.ofNullable(accounts.get(id));
  }

  public Account save(Account account) {
    accounts.put(account.id(), account);
    return account;
  }

  /** Vacía el store. Útil para reiniciar a un estado conocido entre pruebas. */
  public void clear() {
    accounts.clear();
  }
}
