package com.nexo.transfer;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Puente entre Cucumber y Spring: arranca la aplicación real en un puerto aleatorio
 * para que los escenarios corran de punta a punta (HTTP real contra la API), no contra
 * mocks. Es la única clase que declara el contexto; los step definitions se inyectan
 * desde acá.
 */
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberSpringConfiguration {
}
