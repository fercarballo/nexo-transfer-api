package com.nexo.transfer.domain;

import java.math.BigDecimal;

/**
 * Cuenta ficticia. El saldo es mutable porque una transferencia lo modifica;
 * las operaciones {@link #debit} y {@link #credit} encapsulan esa mutación y
 * validan la moneda, evitando que la lógica de saldo se disperse por el servicio.
 */
public class Account {

  private final String id;
  private final String ownerId;
  private final String currency;
  private BigDecimal balance;

  public Account(String id, String ownerId, String currency, BigDecimal balance) {
    this.id = id;
    this.ownerId = ownerId;
    this.currency = currency;
    this.balance = balance;
  }

  public String id() {
    return id;
  }

  public String ownerId() {
    return ownerId;
  }

  public String currency() {
    return currency;
  }

  public BigDecimal balance() {
    return balance;
  }

  public boolean hasSufficientFunds(BigDecimal amount) {
    return balance.compareTo(amount) >= 0;
  }

  public void debit(BigDecimal amount) {
    this.balance = this.balance.subtract(amount);
  }

  public void credit(BigDecimal amount) {
    this.balance = this.balance.add(amount);
  }
}
