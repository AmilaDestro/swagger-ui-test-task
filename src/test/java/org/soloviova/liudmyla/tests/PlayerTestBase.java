package org.soloviova.liudmyla.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.soloviova.liudmyla.entities.Player;
import org.soloviova.liudmyla.entities.PlayerItem;
import org.soloviova.liudmyla.httpclients.PlayerControllerHttpClient;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

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
    protected final List<Integer> playersToDelete;
    protected final PlayerControllerHttpClient httpClient;
    protected final String supervisorLogin;
    protected final Integer supervisorId;
    protected String adminLogin;
    private final Player defaultSupervisorCondition;

    PlayerTestBase() {
        httpClient = PlayerControllerHttpClient.getInstance();
        supervisorLogin = "supervisor";
        supervisorId = getPlayerIdByLogin(supervisorLogin);
        playersToDelete = new ArrayList<>();
        defaultSupervisorCondition = httpClient.getPlayerByIdSuppressRequestException(supervisorId);
    }

    @BeforeClass
    public void setupBeforeAllTests() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeMethod
    public void setupBeforeEachTest() {
        createTestAdmin();
    }

    @AfterMethod
    public void cleanUpAfterEachTest() {
        deleteCreatedPlayers();
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
    protected Response createPlayerSafely(final Player player, final String editor) {
        final Response response = httpClient.createPlayer(player, editor);
        if (isStatusCodeOk(response)) {
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
     * @param editor   login of a user who is going to delete the player
     * @return {@link Response} obtained after execution of the HTTP request
     */
    protected Response deletePlayerSafely(final Integer playerId, final String editor) {
        log.info("Performing safe delete of Player {}", playerId);
        Optional<Integer> playerIdIsPreparedForDeletion = playersToDelete.stream()
                .filter(id -> Objects.equals(id, playerId))
                .findFirst();
        Response response = httpClient.deletePlayer(playerId, editor);

        if (playerIdIsPreparedForDeletion.isPresent() && isStatusCodeOk(response)) {
            playersToDelete.remove(playerId);
        }
        return response;
    }

    protected String getPlayerLogin(final Integer playerId) {
        return httpClient.getPlayerByIdSuppressRequestException(playerId).getLogin();
    }

    protected String getAdminLogin() {
        return getPlayerLogin(getPlayerItemWithRole("admin").getId());
    }

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
     * Checks whether specified Player is available in all players list by id
     *
     * @param playerId     {@link Player} which is being checked
     * @param shouldBeAvailable boolean parameter which indicates whether the specified player is expected to be
     *                          available in all players list
     */
    protected void checkIfPlayerIsAvailableInAllPlayersList(final Integer playerId,
                                                            final boolean shouldBeAvailable) {
        assertEquals(createdPlayerIsFoundAmongTheListOfAllPlayerItems(playerId), shouldBeAvailable);
    }

    protected void checkIfPlayerIsAvailableInAllPlayersList(final String playerLogin,
                                                            final boolean shouldBeAvailable) {
        assertEquals(createdPlayerIsFoundAmongTheListOfAllPlayerItems(playerLogin), shouldBeAvailable);
    }

    private boolean createdPlayerIsFoundAmongTheListOfAllPlayerItems(final Integer playerId) {
        log.info("Checking if Player with id {} can be found among the list of all PlayerItems", playerId);
        Optional<PlayerItem> isPlayerItemFound = httpClient.getAllPlayersSuppressRequestException()
                .stream()
                .filter(playerItem -> Objects.equals(playerItem.getId(), playerId))
                .findAny();
        return isPlayerItemFound.isPresent();
    }

    private boolean createdPlayerIsFoundAmongTheListOfAllPlayerItems(final String playerLogin) {
        log.info("Checking if Player with login {} can be found among the list of all PlayerItems", playerLogin);
        Optional<PlayerItem> isPlayerItemFound = httpClient.getAllPlayersSuppressRequestException()
                .stream()
                .filter(playerItem -> getPlayerLogin(playerItem.getId()).equals(playerLogin))
                .findAny();
        return isPlayerItemFound.isPresent();
    }

    private void deleteCreatedPlayers() {
        log.info("Deleting created players during testing");

        playersToDelete.forEach(playerId -> httpClient.deletePlayer(playerId, "supervisor"));
        playersToDelete.clear();

        log.info("Players list is clear");
    }

    private boolean isStatusCodeOk(final Response response) {
        final int statusCode = response.getStatusCode();
        return statusCode >= 200 && statusCode < 205;
    }

    private PlayerItem getPlayerItemWithRole(final String role) {
        return httpClient.getAllPlayersSuppressRequestException()
                .stream()
                .filter(playerItem -> httpClient.getPlayerByIdSuppressRequestException(playerItem.getId())
                        .getRole().equals(role))
                .findFirst()
                .orElseThrow(() -> new AssertionError(format("Player with role [%s]] was not found", role)));
    }

    private void createTestAdmin() {
        final Player adminToCreate = Player.builder()
                .role("admin")
                .screenName("Test_Admin_1")
                .login("testAdmin1")
                .password("vbrhei40fn8")
                .gender("female")
                .age(27)
                .build();

        log.info("Creating admin user for tests: {}", adminToCreate);
        val response = createPlayerSafely(adminToCreate, "supervisor");
        adminLogin = isStatusCodeOk(response) ? "testAdmin1" : getAdminLogin();
    }

    private void restoreSupervisorData() {
        val id = getPlayerIdByLogin(supervisorLogin);
        val supervisorAfterTests = httpClient.getPlayerByIdSuppressRequestException(id);
        if (!supervisorAfterTests.equals(defaultSupervisorCondition)) {
            httpClient.updatePlayer(id, supervisorLogin, defaultSupervisorCondition);
        }
    }
}
