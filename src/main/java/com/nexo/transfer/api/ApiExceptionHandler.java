package com.nexo.transfer.api;

import com.nexo.transfer.domain.TransferExceptions;
import com.nexo.transfer.security.CorrelationIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduce las excepciones de dominio a respuestas HTTP con un cuerpo de error
 * uniforme. Centralizar esto acá mantiene los controladores limpios y garantiza
 * que TODA la API responda errores con el mismo formato y con el requestId de
 * correlación, lo que hace las pruebas negativas simples y estables.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(TransferExceptions.AccountNotFound.class)
  public ResponseEntity<ErrorResponse> handleAccountNotFound(
      TransferExceptions.AccountNotFound ex, HttpServletRequest req) {
    return build(HttpStatus.NOT_FOUND, "ACCOUNT_NOT_FOUND", ex.getMessage(), req);
  }

  @ExceptionHandler(TransferExceptions.NotFound.class)
  public ResponseEntity<ErrorResponse> handleNotFound(
      TransferExceptions.NotFound ex, HttpServletRequest req) {
    return build(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), req);
  }

  @ExceptionHandler(TransferExceptions.InsufficientFunds.class)
  public ResponseEntity<ErrorResponse> handleInsufficientFunds(
      TransferExceptions.InsufficientFunds ex, HttpServletRequest req) {
    return build(HttpStatus.UNPROCESSABLE_ENTITY, "INSUFFICIENT_FUNDS", ex.getMessage(), req);
  }

  @ExceptionHandler(TransferExceptions.Forbidden.class)
  public ResponseEntity<ErrorResponse> handleForbidden(
      TransferExceptions.Forbidden ex, HttpServletRequest req) {
    return build(HttpStatus.FORBIDDEN, "FORBIDDEN", ex.getMessage(), req);
  }

  @ExceptionHandler(TransferExceptions.Invalid.class)
  public ResponseEntity<ErrorResponse> handleInvalid(
      TransferExceptions.Invalid ex, HttpServletRequest req) {
    return build(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", ex.getMessage(), req);
  }

  @ExceptionHandler(TransferExceptions.IdempotencyConflict.class)
  public ResponseEntity<ErrorResponse> handleIdempotencyConflict(
      TransferExceptions.IdempotencyConflict ex, HttpServletRequest req) {
    return build(HttpStatus.CONFLICT, "IDEMPOTENCY_CONFLICT", ex.getMessage(), req);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(
      MethodArgumentNotValidException ex, HttpServletRequest req) {
    String message = ex.getBindingResult().getFieldErrors().stream()
        .map(e -> e.getField() + ": " + e.getDefaultMessage())
        .collect(Collectors.joining("; "));
    return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message, req);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest req) {
    return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
        "Ocurrió un error inesperado.", req);
  }

  private ResponseEntity<ErrorResponse> build(HttpStatus status, String code, String message,
      HttpServletRequest req) {
    Object requestId = req.getAttribute(CorrelationIdFilter.REQUEST_ID_ATTRIBUTE);
    return ResponseEntity.status(status).body(
        ErrorResponse.of(code, message, requestId == null ? null : requestId.toString()));
  }
}
