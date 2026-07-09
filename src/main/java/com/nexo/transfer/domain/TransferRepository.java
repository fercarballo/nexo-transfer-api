package com.nexo.transfer.domain;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de transferencias en memoria (MVP). Además del índice por id, mantiene
 * un índice por Idempotency-Key para resolver reintentos sin recorrer todo el store.
 */
@Repository
public class TransferRepository {

  private final ConcurrentHashMap<String, Transfer> byId = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, String> idByIdempotencyKey = new ConcurrentHashMap<>();

  public Optional<Transfer> findById(String id) {
    return Optional.ofNullable(byId.get(id));
  }

  public Optional<Transfer> findByIdempotencyKey(String key) {
    return Optional.ofNullable(idByIdempotencyKey.get(key)).map(byId::get);
  }

  public Transfer save(Transfer transfer) {
    byId.put(transfer.id(), transfer);
    if (transfer.idempotencyKey() != null && !transfer.idempotencyKey().isBlank()) {
      idByIdempotencyKey.put(transfer.idempotencyKey(), transfer.id());
    }
    return transfer;
  }

  /** Vacía el store (transferencias e índice de idempotencia). */
  public void clear() {
    byId.clear();
    idByIdempotencyKey.clear();
  }
}
