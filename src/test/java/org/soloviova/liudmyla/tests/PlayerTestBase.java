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
    protected final PlayerControllerHttpClient httpClient = PlayerControllerHttpClient.getInstance();

    @BeforeClass
    public void setupBeforeTests() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @AfterMethod
    public void cleanUpAfterTests() {
        deleteCreatedPlayers();
    }

    /**
     * Creates a new Player in the app and adds its id to the list for deletion after test
     * if the Player was created successfully
     *
     * @param player {@link Player} entity to create
     * @param editor role of a user who is going to create a new player (user, admin or supervisor)
     *
     * @return {@link Response} obtained after execution of the HTTP request
     */
    protected Response createPlayerSafely(final Player player, final String editor) {
        final Response response = httpClient.createPlayer(player, editor);
        if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
            Player createdPlayer = response.as(Player.class);
            playersToDelete.add(createdPlayer.getId());
        }
        return response;
    }

    /**
     * Deletes the Player with the given id and removes its id from the list for test data clean up
     * if the deletion was successful.
     *
     * @param playerId of the Player to delete
     * @param editor role of a user who is going to delete the player (user, admin or supervisor)
     *
     * @return {@link Response} obtained after execution of the HTTP request
     */
    protected Response deletePlayerSafely(final Integer playerId, final String editor) {
        log.info("Performing safe delete of Player {}", playerId);
        Optional<Integer> playerIdIsPreparedForDeletion = playersToDelete.stream()
                .filter(id -> Objects.equals(id, playerId))
                .findFirst();
        Response response = httpClient.deletePlayer(playerId, editor);

        if (playerIdIsPreparedForDeletion.isPresent() && response.getStatusCode() < 400) {
            playersToDelete.remove(playerId);
        }
        return response;
    }

    /**
     * Checks whether specified Player is available in all players list by id
     *
     * @param createdPlayer {@link Player} which is being checked
     * @param shouldBeAvailable boolean parameter which indicates whether the specified player is expected to be
     *                          available in all players list
     */
    protected void checkIfPlayerIsAvailableInAllPlayersList(final Player createdPlayer,
                                                            final boolean shouldBeAvailable) {
        assertEquals(createdPlayerIsFoundAmongTheListOfAllPlayerItems(createdPlayer), shouldBeAvailable);
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
