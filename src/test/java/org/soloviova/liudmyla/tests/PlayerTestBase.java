package org.soloviova.liudmyla.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.soloviova.liudmyla.entities.Player;
import org.soloviova.liudmyla.entities.PlayerItem;
import org.soloviova.liudmyla.httpclients.PlayerControllerHttpClient;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.lang.String.format;
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
    protected final String supervisorLogin = "supervisor";
    protected final Integer supervisorId;
    protected final String adminLogin = "testAdmin1";
    private final Player defaultSupervisorCondition;
    protected final PlayerControllerHttpClient httpClient;

    PlayerTestBase() {
        synchronized (this) {
            httpClient = PlayerControllerHttpClient.getInstance();
            supervisorId = getPlayerIdByLogin(supervisorLogin);
            defaultSupervisorCondition = httpClient.getPlayerByIdSuppressRequestException(supervisorId);
        }
    }

    @BeforeSuite(alwaysRun = true)
    public synchronized void setupBeforeAllTests() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        createTestAdminIfNotExists();
    }

    @AfterSuite(alwaysRun = true)
    public synchronized void cleanUpAfterTests() {
        deleteCreatedPlayers();
        deleteTestAdmin();
        restoreSupervisorData();
    }

    /**
     * Creates a new Player in the app and adds its id to the list for deletion after test
     * if the Player was created successfully
     *
     * @param player {@link Player} entity to create
     * @param editor login of a user who is going to create the new player
     * @return {@link Response} obtained after execution of the HTTP request
     */
    protected synchronized Response createPlayerSafely(final Player player, final String editor) {
        final Response response = httpClient.createPlayer(player, editor);
        val playerLogin = player.getLogin();
        checkIfPlayerIsAvailableInAllPlayersList(playerLogin, true);

        val playerId = getPlayerIdByLogin(playerLogin);
        playersToDelete.add(playerId);
        log.info("Player with id {} and login {} was safely created and prepared for deletion", playerId, playerLogin);

        return response;
    }

    /**
     * Deletes the Player with the given id and removes its id from the list for test data clean up
     * if the deletion was successful.
     *
     * @param playerId of the Player to delete
     * @param editor   login of a user who is going to delete the player
     * @return {@link Response} obtained after execution of the HTTP request
     */
    protected synchronized Response deletePlayerSafely(final Integer playerId, final String editor) {
        log.info("Performing safe delete of Player {}", playerId);
//        Optional<Integer> playerIdIsPreparedForDeletion = playersToDelete.stream()
//                .filter(id -> Objects.equals(id, playerId))
//                .findFirst();

        Response response = httpClient.deletePlayer(playerId, editor);
        checkIfPlayerIsAvailableInAllPlayersList(playerId, false);

//        if (playerIdIsPreparedForDeletion.isPresent() && isStatusCodeOk(response)) {
            playersToDelete.remove(playerId);
            log.info("Player with id {} was deleted safely", playerId);
//        }
        return response;
    }

    /**
     * Gets login for a Player with the given id
     *
     * @param playerId - id of the Player
     * @return found player's login as {@link String}
     */
    protected String getPlayerLogin(final Integer playerId) {
        return httpClient.getPlayerByIdSuppressRequestException(playerId).getLogin();
    }

    /**
     * Gets id of a Player with the given login
     *
     * @param playerLogin - login of the Player
     * @return id of the Player
     */
    protected Integer getPlayerIdByLogin(final String playerLogin) {
        val player = httpClient.getAllPlayersSuppressRequestException()
                .stream()
                .filter(playerItem -> httpClient.getPlayerByIdSuppressRequestException(playerItem.getId())
                        .getLogin().equals(playerLogin))
                .findFirst()
                .orElseThrow(() -> new AssertionError(format("Player with login [%s] was not found", playerLogin)));
        return player.getId();
    }

    /**
     * Checks whether specified Player is available in all players list by id and compares the result with expected one
     *
     * @param playerId          id of the Player which is being checked
     * @param shouldBeAvailable boolean parameter which indicates whether the specified player is expected to be
     *                          available in all players list
     */
    protected void checkIfPlayerIsAvailableInAllPlayersList(final Integer playerId,
                                                            final boolean shouldBeAvailable) {
        assertEquals(createdPlayerIsFoundAmongTheListOfAllPlayerItems(playerId), shouldBeAvailable);
    }

    /**
     * Checks whether specified Player is available in all players list by id and compares the result with expected one
     *
     * @param playerLogin       login of the Player which is being checked
     * @param shouldBeAvailable boolean parameter which indicates whether the specified player is expected to be
     *                          available in all players list
     */
    protected void checkIfPlayerIsAvailableInAllPlayersList(final String playerLogin,
                                                            final boolean shouldBeAvailable) {
        assertEquals(createdPlayerIsFoundAmongTheListOfAllPlayerItems(playerLogin), shouldBeAvailable);
    }

    /**
     * Checks whether specified Player is available in all players list by id
     *
     * @param playerId - id of the Player which is being checked
     * @return {@link Boolean} result of the check
     */
    private boolean createdPlayerIsFoundAmongTheListOfAllPlayerItems(final Integer playerId) {
        log.info("Checking if Player with id {} can be found among the list of all PlayerItems", playerId);
        Optional<PlayerItem> isPlayerItemFound = httpClient.getAllPlayersSuppressRequestException()
                .stream()
                .filter(playerItem -> Objects.equals(playerItem.getId(), playerId))
                .findAny();
        return isPlayerItemFound.isPresent();
    }

    /**
     * Checks whether specified Player is available in all players list by login
     *
     * @param playerLogin - login of the Player which is being checked
     * @return {@link Boolean} result of the check
     */
    private boolean createdPlayerIsFoundAmongTheListOfAllPlayerItems(final String playerLogin) {
        log.info("Checking if Player with login {} can be found among the list of all PlayerItems", playerLogin);
        Optional<PlayerItem> isPlayerItemFound = httpClient.getAllPlayersSuppressRequestException()
                .stream()
                .filter(playerItem -> getPlayerLogin(playerItem.getId()).equals(playerLogin))
                .findAny();
        return isPlayerItemFound.isPresent();
    }

    /**
     * Deletes only those Players that were created in tests
     */
    private synchronized void deleteCreatedPlayers() {
        log.info("Deleting created players during testing: {}", playersToDelete);

        playersToDelete.forEach(playerId -> {
            deletePlayerSafely(playerId, supervisorLogin);
            checkIfPlayerIsAvailableInAllPlayersList(playerId, false);
        });

        log.info("Players list is clear");
    }

    /**
     * Checks if http request was executed successfully
     *
     * @param response {@link Response} that was obtained after the request rexecution
     * @return {@link Boolean} result of the check
     */
    private boolean isStatusCodeOk(final Response response) {
        final int statusCode = response.getStatusCode();
        return statusCode >= 200 && statusCode < 205;
    }

    /**
     * Looks for a Player with specified role among list of all players
     *
     * @param role - role of the Player to find
     * @return found Player entity as {@link PlayerItem}
     */
    private PlayerItem getPlayerItemWithRole(final String role) {
        return httpClient.getAllPlayersSuppressRequestException()
                .stream()
                .filter(playerItem -> httpClient.getPlayerByIdSuppressRequestException(playerItem.getId())
                        .getRole().equals(role))
                .findFirst()
                .orElseThrow(() -> new AssertionError(format("Player with role [%s]] was not found", role)));
    }

    /**
     * Creates custom admin to be used in tests
     */
    private synchronized void createTestAdminIfNotExists() {
        if (!doesTestAdminExist()) {
            final Player adminToCreate = Player.builder()
                    .role("admin")
                    .screenName("Test_Admin_1")
                    .login(adminLogin)
                    .password("vbrhei40fn8")
                    .gender("female")
                    .age(27)
                    .build();

            log.info("Creating admin user for tests: {}", adminToCreate);
            httpClient.createPlayer(adminToCreate, supervisorLogin);
        }
    }

    /**
     * Restores fields values for the supervisor in case if any data was changed during testing
     */
    private synchronized void restoreSupervisorData() {
        val id = getPlayerIdByLogin(supervisorLogin);
        val supervisorAfterTests = httpClient.getPlayerByIdSuppressRequestException(id);
        if (!supervisorAfterTests.equals(defaultSupervisorCondition)) {
            httpClient.updatePlayer(id, supervisorLogin, defaultSupervisorCondition);
        }
    }

    private synchronized void deleteTestAdmin() {
        if(doesTestAdminExist()) {
            val testAdminId = getPlayerIdByLogin(adminLogin);
            httpClient.deletePlayer(testAdminId, supervisorLogin);
        }
    }

    private boolean doesTestAdminExist() {
        return createdPlayerIsFoundAmongTheListOfAllPlayerItems(adminLogin);
    }
}
