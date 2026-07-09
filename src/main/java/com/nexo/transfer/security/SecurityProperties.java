package com.nexo.transfer.security;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuración de seguridad. El mapa {@code tokens} asocia un bearer token a un
 * usuario. En local se cargan valores de desarrollo desde {@code application.yml};
 * en un entorno real se inyectan por variable de entorno (ver {@code .env.example})
 * y NUNCA se versionan tokens productivos.
 */
@ConfigurationProperties(prefix = "nexo.security")
public class SecurityProperties {

  /** token → userId */
  private Map<String, String> tokens = new HashMap<>();

  public Map<String, String> getTokens() {
    return tokens;
  }

  public void setTokens(Map<String, String> tokens) {
    this.tokens = tokens;
  }
}
