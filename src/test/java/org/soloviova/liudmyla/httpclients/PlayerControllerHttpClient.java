package org.soloviova.liudmyla.httpclients;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.soloviova.liudmyla.entities.Player;
import org.soloviova.liudmyla.entities.PlayerIdItem;
import org.soloviova.liudmyla.entities.PlayerItem;
import org.soloviova.liudmyla.mappers.PlayerMapper;

import java.util.List;

import static io.restassured.RestAssured.given;

/**
 * This class contains common functionality that actually performs create, delete, update and get operations on players
 * but does not contain assertions. All assertions are made in tests.
 *
 * @author Liudmyla Soloviova
 */
@Slf4j
public class PlayerControllerHttpClient {
    public static final String BASE_URL = "http://3.68.165.45/player";
    private static final String GET_PLAYER_BY_ID_URL = BASE_URL + "/get";
    private static final String GET_ALL_PLAYERS_URL = BASE_URL + "/get/all";
    private static final String CREATE_PLAYER_URL = BASE_URL + "/create/{editor}";
    private static final String DELETE_PLAYER_URL = BASE_URL + "/delete/{editor}";
    private static final String UPDATE_PLAYER_URL = BASE_URL + "/update/{editor}/{id}";
    private static PlayerControllerHttpClient playerControllerClient;

    private final PlayerMapper mapper;

    private PlayerControllerHttpClient() {
        mapper = PlayerMapper.getInstance();
    }

    public static synchronized PlayerControllerHttpClient getInstance() {
        if (playerControllerClient == null) {
            playerControllerClient = new PlayerControllerHttpClient();
        }
        return playerControllerClient;
    }

    public synchronized Response getAllPlayers() {
        log.info("Getting list of all registered users");
        final Response response = given().baseUri(GET_ALL_PLAYERS_URL).when().get();
        log.info("Obtained response: {}", response.asPrettyString());

        return response;
    }

    public synchronized List<PlayerItem> getAllPlayersSuppressRequestException() {
        try {
            return getAllPlayers()
                    .then()
                    .extract()
                    .jsonPath()
                    .getList("players", PlayerItem.class);
        } catch (Exception e) {
            log.error("An exception occurred while getting the list of all players:\n{}", e.getMessage());
            return List.of();
        }
    }

    public synchronized Response getPlayerById(final Integer playerId) {
        log.info("Getting a Player with id {}", playerId);
        final PlayerIdItem playerIdItem = new PlayerIdItem(playerId);

        log.info("Executing POST request to endpoint: {}", GET_PLAYER_BY_ID_URL);
        val givenRequestParams = given().contentType(ContentType.JSON).body(playerIdItem);
        log.info("Request body to be sent: {}", mapper.mapPlayerObjectToJsonStringSuppressException(playerIdItem));

        final Response response = givenRequestParams.when().post(GET_PLAYER_BY_ID_URL);
        log.info("Obtained response: {}", response.asPrettyString());

        return response;
    }

    public synchronized Player getPlayerByIdSuppressRequestException(final Integer playerId) {
        try {
            return getPlayerById(playerId).then().extract().as(Player.class);
        } catch (Exception e) {
            log.error("An exception occurred while getting a Player with id {}: {}", playerId, e.getMessage());
            throw new AssertionError(String.format("Player with id %s was not found", playerId));
        }
    }

    public synchronized Response deletePlayer(final Integer playerId, final String editor) {
        log.info("Deleting Player with id {}", playerId);
        final PlayerIdItem playerIdItem = new PlayerIdItem(playerId);

        log.info("Executing DELETE request to endpoint: {}, where editor - {}", DELETE_PLAYER_URL, editor);
        val givenRequestParams = given().contentType(ContentType.JSON).body(playerIdItem);
        log.info("Request body to be sent: {}", mapper.mapPlayerObjectToJsonStringSuppressException(playerIdItem));

        final Response response = givenRequestParams.when().delete(DELETE_PLAYER_URL, editor);
        log.info("Obtained response: {}", response.asPrettyString());

        return response;
    }

    public synchronized Response createPlayer(final Player player, final String editor) {
        log.info("Creating a new Player: {}", player.toString());
        log.info("Executing GET request to endpoint: {}, where editor - {}", CREATE_PLAYER_URL, editor);

        val givenRequestParams = given()
                .param("age", player.getAge())
                .param("gender", player.getGender())
                .param("login", player.getLogin())
                .param("password", player.getPassword())
                .param("role", player.getRole())
                .param("screenName", player.getScreenName());
        log.info("Request params: {}", givenRequestParams.log().params());

        final Response response = givenRequestParams.get(CREATE_PLAYER_URL, editor);
        log.info("Obtained response: {}", response.asPrettyString());

        return response;
    }

    public synchronized Response updatePlayer(final Integer playerId,
                                              final String editorLogin,
                                              final Player updatedPlayer) {
        log.info("Updating player with id {} by editor {}. Data to update: {}",
                playerId, editorLogin, updatedPlayer.toString());
        log.info("Executing PATCH request to endpoint: {}, where editor - {}, id - {}",
                UPDATE_PLAYER_URL, editorLogin, playerId);
        log.info("Request body to be sent: {}", mapper.mapPlayerObjectToJsonStringSuppressException(updatedPlayer));

        val response = given()
                .contentType(ContentType.JSON)
                .body(updatedPlayer)
                .when()
                .patch(UPDATE_PLAYER_URL, editorLogin, playerId);

        log.info("Obtained response: {}", response.asPrettyString());

        return response;
    }
}
