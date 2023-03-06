package org.soloviova.liudmyla.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.soloviova.liudmyla.entities.Player;
import org.soloviova.liudmyla.entities.PlayerItem;
import org.soloviova.liudmyla.httpclients.PlayerControllerHttpClient;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.testng.Assert.assertEquals;

/**
 * This is a test base class for Player Controller. It contains some common methods for verifications and
 * safe create/delete methods that can be used for test data clean up.
 *
 * @author Liudmyla Soloviova
 */
@Slf4j
public abstract class PlayerTestBase {
    protected final List<Integer> playersToDelete = new ArrayList<>();
    protected final PlayerControllerHttpClient httpClient = new PlayerControllerHttpClient();

    @BeforeClass
    public void setupBeforeTests() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @AfterMethod
    public void cleanUpAfterTests() {
        deleteCreatedPlayers();
    }

    protected Response createPlayerSafely(final Player player, final String editor) {
        final Response response = httpClient.createPlayer(player, editor);
        if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
            Player createdPlayer = response.jsonPath().getObject("", Player.class);
            playersToDelete.add(createdPlayer.getId());
        }
        return response;
    }

    protected Response deletePlayerSafely(final Integer playerId) {
        log.info("Performing safe delete of Player {}", playerId);
        Optional<Integer> playerIdIsPreparedForDeletion = playersToDelete.stream()
                .filter(id -> Objects.equals(id, playerId))
                .findFirst();
        Response response = httpClient.deletePlayer(playerId, "supervisor");

        if (playerIdIsPreparedForDeletion.isPresent()) {
            playersToDelete.remove(playerId);
        }
        return response;
    }

    protected void verifyThatPlayerIsPresentInPlayerItemsList(final Player createdPlayer,
                                                              final boolean shouldBeFound) {
        assertEquals(createdPlayerIsFoundAmongTheListOfAllPlayerItems(createdPlayer), shouldBeFound);
    }

    private boolean createdPlayerIsFoundAmongTheListOfAllPlayerItems(final Player createdPlayer) {
        log.info("Checking if Player {} can be found among the list of all PlayerItems", createdPlayer);
        Optional<PlayerItem> isPlayerItemFound = httpClient.getAllPlayersSuppressRequestException()
                .stream()
                .filter(playerItem -> Objects.equals(playerItem.getId(), createdPlayer.getId()))
                .findAny();
        return isPlayerItemFound.isPresent();
    }

    private void deleteCreatedPlayers() {
        log.info("Deleting created players during testing");

        playersToDelete.forEach(playerId -> httpClient.deletePlayer(playerId, "supervisor"));
        playersToDelete.clear();

        log.info("Players list is clear");
    }
}
