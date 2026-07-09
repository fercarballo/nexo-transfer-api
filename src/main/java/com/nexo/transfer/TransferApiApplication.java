package com.nexo.transfer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Punto de entrada de nexo-transfer-api.
 *
 * <p>API de transferencias ficticias del ecosistema Nexo Finanzas. Es el núcleo
 * del portfolio: define el contrato del que dependen los canales web y mobile.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class TransferApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(TransferApiApplication.class, args);
  }
}
