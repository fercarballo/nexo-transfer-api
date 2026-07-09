package com.nexo.transfer.api;

import com.nexo.transfer.domain.Account;
import com.nexo.transfer.domain.AccountRepository;
import com.nexo.transfer.domain.CreateTransferCommand;
import com.nexo.transfer.domain.Transfer;
import com.nexo.transfer.domain.TransferExceptions;
import com.nexo.transfer.domain.TransferRepository;
import com.nexo.transfer.domain.TransferService;
import com.nexo.transfer.security.TokenAuthFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Endpoints de transferencias. */
@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {

  private final TransferService transferService;
  private final TransferRepository transfers;
  private final AccountRepository accounts;

  public TransferController(TransferService transferService, TransferRepository transfers,
      AccountRepository accounts) {
    this.transferService = transferService;
    this.transfers = transfers;
    this.accounts = accounts;
  }

  @PostMapping
  public ResponseEntity<TransferView> create(
      @Valid @RequestBody CreateTransferRequest request,
      @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
      HttpServletRequest httpRequest) {

    String userId = (String) httpRequest.getAttribute(TokenAuthFilter.USER_ID_ATTRIBUTE);
    var command = new CreateTransferCommand(
        userId,
        request.sourceAccountId(),
        request.destinationAccountId(),
        request.amount(),
        request.currency(),
        idempotencyKey);

    Transfer transfer = transferService.createTransfer(command);
    return ResponseEntity
        .created(URI.create("/api/v1/transfers/" + transfer.id()))
        .body(TransferView.from(transfer));
  }

  @GetMapping("/{id}")
  public TransferView getById(@PathVariable String id, HttpServletRequest httpRequest) {
    String userId = (String) httpRequest.getAttribute(TokenAuthFilter.USER_ID_ATTRIBUTE);
    Transfer transfer = transfers.findById(id)
        .orElseThrow(() -> new TransferExceptions.NotFound("La transferencia no existe: " + id));
    if (!isInvolved(userId, transfer)) {
      throw new TransferExceptions.Forbidden("La transferencia no pertenece al usuario autenticado.");
    }
    return TransferView.from(transfer);
  }

  /** El usuario puede ver la transferencia si es dueño de la cuenta de origen o de destino. */
  private boolean isInvolved(String userId, Transfer transfer) {
    boolean ownsSource = accounts.findById(transfer.sourceAccountId())
        .map(Account::ownerId).map(userId::equals).orElse(false);
    boolean ownsDestination = accounts.findById(transfer.destinationAccountId())
        .map(Account::ownerId).map(userId::equals).orElse(false);
    return ownsSource || ownsDestination;
  }
}
