package org.soloviova.liudmyla.tests;

import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.soloviova.liudmyla.httpclients.PlayerControllerHttpClient;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class PlayerTestBase {
    protected final List<Integer> playersToDelete = new ArrayList<>();
    protected final PlayerControllerHttpClient httpClient = new PlayerControllerHttpClient();

    @BeforeClass
    public void setupBeforeTests() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @AfterClass
    public void cleanUpAfterTests() {
        deleteCreatedPlayers();
    }

    private void deleteCreatedPlayers() {
        log.info("Deleting created players during testing");

        playersToDelete.forEach(playerId -> httpClient.deletePlayer(playerId, "supervisor"));
        playersToDelete.clear();

        log.info("Players list is clear");
    }
}
