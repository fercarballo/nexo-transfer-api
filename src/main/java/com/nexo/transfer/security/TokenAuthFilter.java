package com.nexo.transfer.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexo.transfer.api.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Autenticación por bearer token (MVP). Valida el header {@code Authorization} contra
 * el mapa de tokens configurado y, si es válido, publica el {@code userId} como atributo
 * del request para que los controladores decidan la autorización.
 *
 * <p>Es deliberadamente simple (no JWT ni OAuth) porque el foco del portfolio es la
 * automatización de pruebas, no un proveedor de identidad. El diseño por interfaz de
 * atributos permite reemplazarlo por Spring Security real sin tocar los controladores
 * (ver ADR-002).
 */
@Component
@Order(2)
public class TokenAuthFilter extends OncePerRequestFilter {

  public static final String USER_ID_ATTRIBUTE = "nexo.userId";
  private static final String BEARER_PREFIX = "Bearer ";

  private final SecurityProperties properties;
  private final ObjectMapper objectMapper;

  public TokenAuthFilter(SecurityProperties properties, ObjectMapper objectMapper) {
    this.properties = properties;
    this.objectMapper = objectMapper;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    return path.equals("/")
        || path.startsWith("/actuator")
        || path.startsWith("/swagger-ui")
        || path.startsWith("/v3/api-docs");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws ServletException, IOException {
    String header = request.getHeader("Authorization");
    if (header == null || !header.startsWith(BEARER_PREFIX)) {
      unauthorized(request, response, "Falta el header Authorization: Bearer <token>.");
      return;
    }
    String token = header.substring(BEARER_PREFIX.length()).trim();
    String userId = properties.getTokens().get(token);
    if (userId == null) {
      unauthorized(request, response, "Token inválido.");
      return;
    }
    request.setAttribute(USER_ID_ATTRIBUTE, userId);
    chain.doFilter(request, response);
  }

  private void unauthorized(HttpServletRequest request, HttpServletResponse response, String message)
      throws IOException {
    Object requestId = request.getAttribute(CorrelationIdFilter.REQUEST_ID_ATTRIBUTE);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");
    ErrorResponse body = ErrorResponse.of("UNAUTHORIZED", message,
        requestId == null ? null : requestId.toString());
    response.getWriter().write(objectMapper.writeValueAsString(body));
  }
}
