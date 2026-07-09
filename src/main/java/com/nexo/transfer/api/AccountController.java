package com.nexo.transfer.api;

import com.nexo.transfer.domain.Account;
import com.nexo.transfer.domain.AccountRepository;
import com.nexo.transfer.domain.TransferExceptions;
import com.nexo.transfer.security.TokenAuthFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Endpoints de cuentas (solo lectura en el MVP). */
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

  private final AccountRepository accounts;

  public AccountController(AccountRepository accounts) {
    this.accounts = accounts;
  }

  @GetMapping("/{id}/balance")
  public BalanceView getBalance(@PathVariable String id, HttpServletRequest httpRequest) {
    String userId = (String) httpRequest.getAttribute(TokenAuthFilter.USER_ID_ATTRIBUTE);
    Account account = accounts.findById(id)
        .orElseThrow(() -> new TransferExceptions.AccountNotFound(id));
    if (!account.ownerId().equals(userId)) {
      throw new TransferExceptions.Forbidden("La cuenta no pertenece al usuario autenticado.");
    }
    return new BalanceView(account.id(), account.balance(), account.currency());
  }
}
