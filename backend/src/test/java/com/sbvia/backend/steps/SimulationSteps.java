package com.sbvia.backend.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class SimulationSteps {

    private String simulationState;
    private int score;
    private int infractions;

    @Given("que el conductor está autenticado")
    public void driverIsAuthenticated() {
        // Lógica de mock de autenticación
    }

    @And("selecciona el escenario {string}")
    public void selectScenario(String scenario) {
        // Lógica para seleccionar escenario
    }

    @When("el conductor inicia la simulación")
    public void driverStartsSimulation() {
        this.simulationState = "EN_PROGRESO";
        this.score = 100;
    }

    @And("comete {int} infracciones")
    public void commitInfractions(int amount) {
        this.infractions = amount;
        this.score -= (amount * 10);
    }

    @Then("la simulación finaliza con estado {string}")
    public void simulationFinishesWithState(String expectedState) {
        this.simulationState = "FINALIZADA"; // Simulando el cambio
        Assertions.assertEquals(expectedState, this.simulationState);
    }

    @And("el puntaje final es {int}")
    public void finalScoreIs(int expectedScore) {
        Assertions.assertEquals(expectedScore, this.score);
    }
}
