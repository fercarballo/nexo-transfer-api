package com.nexo.transfer;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Runner que integra Cucumber en el ciclo de Maven (surefire/failsafe) vía JUnit Platform.
 * Descubre los .feature de {@code src/test/resources/features} y usa como glue el paquete
 * {@code com.nexo.transfer}. Genera reportes HTML y JSON como evidencia.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.nexo.transfer")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value = "pretty, html:target/cucumber/report.html, json:target/cucumber/report.json")
public class RunCucumberTest {
}
