package com.nexo.transfer.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Trazabilidad: asigna (o respeta) un identificador de correlación por request.
 *
 * <p>Lo publica en tres lugares: el MDC del logging (para que cada línea de log lo
 * lleve), un atributo del request (para el manejador de errores) y el header de
 * respuesta {@code X-Request-Id} (para que el cliente pueda reportarlo). Corre
 * primero (orden alto) para que todo lo demás ya tenga el id disponible.
 */
@Component
@Order(1)
public class CorrelationIdFilter extends OncePerRequestFilter {

  public static final String HEADER = "X-Request-Id";
  public static final String REQUEST_ID_ATTRIBUTE = "nexo.requestId";
  private static final String MDC_KEY = "requestId";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws ServletException, IOException {
    String requestId = request.getHeader(HEADER);
    if (requestId == null || requestId.isBlank()) {
      requestId = UUID.randomUUID().toString();
    }
    MDC.put(MDC_KEY, requestId);
    request.setAttribute(REQUEST_ID_ATTRIBUTE, requestId);
    response.setHeader(HEADER, requestId);
    try {
      chain.doFilter(request, response);
    } finally {
      MDC.remove(MDC_KEY);
    }
  }
}
