package com.nexo.transfer.api;

import java.time.Instant;

/**
 * Cuerpo de error uniforme para toda la API. Un formato único de error simplifica
 * el consumo (web, mobile) y la automatización de pruebas negativas.
 *
 * @param code      código estable y legible por máquina (ej: INSUFFICIENT_FUNDS)
 * @param message   mensaje legible por humanos
 * @param requestId identificador de correlación para trazar el error en los logs
 */
public record ErrorResponse(String code, String message, String requestId, Instant timestamp) {

  public static ErrorResponse of(String code, String message, String requestId) {
    return new ErrorResponse(code, message, requestId, Instant.now());
  }
}
