package org.soloviova.liudmyla.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.soloviova.liudmyla.entities.Player;
import org.soloviova.liudmyla.entities.PlayerIdItem;
import org.soloviova.liudmyla.entities.PlayerItem;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.in;

@Slf4j
public abstract class PlayersControllerTestBase {
    protected final String BASE_URL = "http://3.68.165.45/player";
    protected final String GET_PLAYER_BY_ID_URL = BASE_URL + "/get";
    protected final String GET_ALL_PLAYERS_URL = BASE_URL + "/get/all";
    protected final String CREATE_PLAYER_URL = BASE_URL + "/create/{editor}";
    protected final String DELETE_PLAYER_URL = BASE_URL + "/delete/{editor}";
    protected final List<Integer> playersToDelete = new ArrayList<>();

    @BeforeClass
    public void setupBeforeTests() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @AfterClass
    public void cleanUpAfterTests() {
        deleteCreatedUsers();
    }

    protected List<PlayerItem> getAllPlayerItemsWithoutCheck() {
        return  when()
                .get(GET_ALL_PLAYERS_URL)
                .then()
                .extract()
                .response()
                .jsonPath()
                .getList("players", PlayerItem.class);
    }

    protected Player getPlayerById(final Integer playerId) {
        val requestBody = new PlayerIdItem(playerId);

        return given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(GET_PLAYER_BY_ID_URL)
                .then()
                .extract()
                .jsonPath()
                .getObject("", Player.class);
    }

    protected void deletePlayerById(final Integer playerId) {
        log.info("Deleting Player with id {}", playerId);
        given()
                .contentType(ContentType.JSON)
                .body(new PlayerIdItem(playerId))
                .when()
                .delete(DELETE_PLAYER_URL, "supervisor")
                .then()
                .statusCode(in(List.of(200, 204)));
    }

    protected void deleteCreatedUsers() {
        log.info("Deleting created players during testing");

        playersToDelete.forEach(this::deletePlayerById);
        playersToDelete.clear();

        log.info("Players list is clear");
    }
}
