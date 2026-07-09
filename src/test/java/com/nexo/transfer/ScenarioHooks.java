package com.nexo.transfer;

import com.nexo.transfer.config.DataSeeder;
import com.nexo.transfer.domain.AccountRepository;
import com.nexo.transfer.domain.TransferRepository;
import io.cucumber.java.Before;

/**
 * Aísla los escenarios entre sí. El store en memoria es compartido por todo el contexto
 * de Spring, así que sin este reinicio el estado de un escenario contaminaría al siguiente
 * (ej: una cuenta drenada por una transferencia previa). Antes de cada escenario se vacía
 * el store y se vuelve a sembrar el estado inicial conocido: pruebas deterministas e
 * independientes del orden de ejecución.
 */
public class ScenarioHooks {

  private final AccountRepository accounts;
  private final TransferRepository transfers;
  private final DataSeeder dataSeeder;

  public ScenarioHooks(AccountRepository accounts, TransferRepository transfers,
      DataSeeder dataSeeder) {
    this.accounts = accounts;
    this.transfers = transfers;
    this.dataSeeder = dataSeeder;
  }

  @Before
  public void resetState() {
    accounts.clear();
    transfers.clear();
    dataSeeder.seed();
  }
}
