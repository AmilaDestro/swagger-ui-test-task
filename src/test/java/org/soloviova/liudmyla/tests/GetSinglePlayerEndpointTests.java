package org.soloviova.liudmyla.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.http.ContentType;
import lombok.val;
import org.soloviova.liudmyla.entities.Player;
import org.soloviova.liudmyla.testdata.TestDataProviders;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Contains tests related to Get Player by id operations
 *
 * @author Liudmyla Soloviova
 */
@Feature("Get a single Player by playerId endpoint tests at POST /player/get")
public class GetSinglePlayerEndpointTests extends PlayerTestBase {

    @Description("Test that an existing Player from all players list can be found by his/her playerId.")
    @Test(description = "Check that a player with id from the common list can be found by its id and returned" +
            "as a single entity")
    public void getExistingPlayerByIdAndVerifyItMatchesPlayerItemFromTheList() {
        val allPlayerItems = httpClient.getAllPlayersSuppressRequestException();
        assertTrue(allPlayerItems.size() > 0);

        val existingPlayerItem = allPlayerItems.get(0);
        val playerId = existingPlayerItem.getId();

        val response = httpClient.getPlayerById(playerId)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", notNullValue())
                .extract()
                .response();

        Player player = response.as(Player.class);

        assertEquals(player.getId(), playerId, "'id' of the found Player doesn't match");
        assertEquals(player.getAge(), existingPlayerItem.getAge(), "'age' of the found Player doesn't match");
        assertEquals(player.getScreenName(), existingPlayerItem.getScreenName(),
                "'screenName' of the found Player doesn't match");
        assertEquals(player.getGender(), existingPlayerItem.getGender(),
                "'gender' of the found Player doesn't match");
    }

    @Description("Test that attempt to get a Player by wrong/non-existing playerId leads to the error with 404 status code.")
    @Severity(SeverityLevel.NORMAL)
    @Test(dataProvider = "wrongPlayerIds", dataProviderClass = TestDataProviders.class,
    description = "Check that attempt to get a player by wrong id leads to 404 status code")
    public void verifyGetPlayerByNonExistingIdLeadsTo404StatusCode(final Integer nonExistingId) {
        httpClient.getPlayerById(nonExistingId)
                .then()
                .statusCode(404);
    }
}
