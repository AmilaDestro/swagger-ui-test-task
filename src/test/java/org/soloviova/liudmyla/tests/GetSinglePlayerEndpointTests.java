package org.soloviova.liudmyla.tests;

import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.soloviova.liudmyla.entities.Player;
import org.soloviova.liudmyla.entities.PlayerIdItem;
import org.soloviova.liudmyla.mappers.PlayerMapper;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Slf4j
public class GetSinglePlayerEndpointTests extends PlayersControllerTestBase {

    private final String GET_PLAYER_BY_ID_URL = BASE_URL + "/get";

    @Test
    public void executeRequestToGetExistingPlayerByIdAndVerifyItMatchesPlayerItemFromTheList() {
        val allPlayerItems = getAllPlayerItemsWithoutCheck();
        assertTrue(allPlayerItems.size() > 0);

        val existingPlayerItem = allPlayerItems.get(0);
        val playerId = existingPlayerItem.getId();
        val playerIdBody = new PlayerIdItem(playerId);

        log.info("Trying to get a Player with id {}", playerId);
        log.info("Executing POST request to endpoint: {}", GET_PLAYER_BY_ID_URL);

        val requestParams = given()
                .contentType(ContentType.JSON)
                .body(playerIdBody);

        log.info("Request body to be sent:\n{}", PlayerMapper.getInstance()
                                                             .mapPlayerObjectToJsonStringSuppressException(playerIdBody));

        val response = requestParams
                .when()
                .post(GET_PLAYER_BY_ID_URL)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", notNullValue())
                .extract()
                .response();

        log.info("Obtained response:\n{}", response.asPrettyString());

        Player player = response.jsonPath().getObject("", Player.class);

        assertEquals(player.getId(), playerId, "'id' of the found Player doesn't match");
        assertEquals(player.getAge(), existingPlayerItem.getAge(), "'age' of the found Player doesn't match");
        assertEquals(player.getScreenName(), existingPlayerItem.getScreenName(),
                "'screenName' of the found Player doesn't match");
        assertEquals(player.getGender(), existingPlayerItem.getGender(),
                "'gender' of the found Player doesn't match");
    }

    @Test
    public void verifyGetPlayerByNonExistingIdLeadsTo404StatusCode() {
        val nonExistingId = 0;

        val playerIdBody = new PlayerIdItem(nonExistingId);

        log.info("Trying to get a Player with non-existing id {}", nonExistingId);
        log.info("Executing POST request to endpoint: {}", GET_PLAYER_BY_ID_URL);

        val requestParams = given()
                .contentType(ContentType.JSON)
                .body(playerIdBody);

        log.info("Request body to be sent:\n{}", PlayerMapper.getInstance()
                .mapPlayerObjectToJsonStringSuppressException(playerIdBody));

        requestParams
                .when()
                .post(GET_PLAYER_BY_ID_URL)
                .then()
                .statusCode(404);
    }
}
