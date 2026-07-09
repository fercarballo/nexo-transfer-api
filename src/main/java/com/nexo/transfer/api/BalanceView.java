package com.nexo.transfer.api;

import java.math.BigDecimal;

/** Saldo de una cuenta expuesto por la API. */
public record BalanceView(String accountId, BigDecimal balance, String currency) {
}
