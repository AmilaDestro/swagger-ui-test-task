package org.soloviova.liudmyla.tests;

import io.restassured.http.ContentType;
import lombok.val;
import org.soloviova.liudmyla.entities.Player;
import org.soloviova.liudmyla.testdata.TestDataProviders;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class GetSinglePlayerEndpointTests extends PlayerTestBase {

    @Test
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

        Player player = response.jsonPath().getObject("", Player.class);

        assertEquals(player.getId(), playerId, "'id' of the found Player doesn't match");
        assertEquals(player.getAge(), existingPlayerItem.getAge(), "'age' of the found Player doesn't match");
        assertEquals(player.getScreenName(), existingPlayerItem.getScreenName(),
                "'screenName' of the found Player doesn't match");
        assertEquals(player.getGender(), existingPlayerItem.getGender(),
                "'gender' of the found Player doesn't match");
    }

    @Test(dataProvider = "wrongPlayerIds", dataProviderClass = TestDataProviders.class)
    public void verifyGetPlayerByNonExistingIdLeadsTo404StatusCode(final Integer nonExistingId) {
        httpClient.getPlayerById(nonExistingId)
                .then()
                .statusCode(404);
    }
}
