package com.nexo.transfer;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * Step definitions de la feature de transferencias. Ejercitan la API real por HTTP con
 * REST-assured contra el puerto aleatorio que levanta {@link CucumberSpringConfiguration}.
 * El estado por escenario vive en campos de instancia (Cucumber crea una instancia por
 * escenario), lo que mantiene los pasos independientes entre sí.
 */
public class TransferStepDefinitions {

  @LocalServerPort
  private int port;

  private String token;
  private boolean sendToken = true;
  private Response response;
  private final List<Response> responses = new ArrayList<>();

  @Dado("que el usuario {string} está autenticado con el token {string}")
  public void usuarioAutenticado(String userId, String bearerToken) {
    this.token = bearerToken;
    this.sendToken = true;
  }

  @Dado("que el cliente no envía token")
  public void clienteSinToken() {
    this.sendToken = false;
  }

  @Cuando("transfiere {bigdecimal} {word} desde {string} hacia {string}")
  public void transfiere(BigDecimal amount, String currency, String from, String to) {
    ejecutarTransferencia(amount, currency, from, to, null);
  }

  @Cuando("transfiere {bigdecimal} {word} desde {string} hacia {string} con Idempotency-Key {string}")
  public void transfiereConClave(BigDecimal amount, String currency, String from, String to, String key) {
    ejecutarTransferencia(amount, currency, from, to, key);
  }

  private void ejecutarTransferencia(BigDecimal amount, String currency, String from, String to,
      String idempotencyKey) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("sourceAccountId", from);
    body.put("destinationAccountId", to);
    body.put("amount", amount);
    body.put("currency", currency);

    RequestSpecification req = RestAssured.given()
        .baseUri("http://localhost").port(port)
        .contentType("application/json");
    if (sendToken && token != null) {
      req.header("Authorization", "Bearer " + token);
    }
    if (idempotencyKey != null) {
      req.header("Idempotency-Key", idempotencyKey);
    }
    response = req.body(body).post("/api/v1/transfers");
    responses.add(response);
  }

  @Entonces("la respuesta tiene estado {int}")
  public void laRespuestaTieneEstado(int status) {
    assertThat(response.statusCode()).isEqualTo(status);
  }

  @Entonces("la transferencia queda en estado {string}")
  public void laTransferenciaQuedaEnEstado(String estado) {
    assertThat(response.jsonPath().getString("status")).isEqualTo(estado);
  }

  @Entonces("el código de error es {string}")
  public void elCodigoDeErrorEs(String code) {
    assertThat(response.jsonPath().getString("code")).isEqualTo(code);
  }

  @Entonces("ambas respuestas refieren a la misma transferencia")
  public void ambasRespuestasMismaTransferencia() {
    assertThat(responses).hasSize(2);
    String firstId = responses.get(0).jsonPath().getString("id");
    String secondId = responses.get(1).jsonPath().getString("id");
    assertThat(firstId).isNotNull().isEqualTo(secondId);
  }
}
