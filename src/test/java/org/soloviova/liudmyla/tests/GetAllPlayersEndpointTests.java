package org.soloviova.liudmyla.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.soloviova.liudmyla.entities.PlayerItem;
import org.soloviova.liudmyla.testdata.TestDataProviders;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.notNullValue;
import static org.soloviova.liudmyla.httpclients.PlayerControllerHttpClient.BASE_URL;
import static org.testng.Assert.assertTrue;

/**
 * This class contains tests related to getAllPlayers controller
 *
 * @author Liudmyla Soloviova
 */
@Slf4j
@Feature("Get All Players endpoint tests at GET /player/get/all")
public class GetAllPlayersEndpointTests extends PlayerTestBase {

    @Description("Positive test for getAllPlayers endpoint. Checks that the request returns non-empty list of all players.")
    @Test(description = "Check that getAllPlayers endpoint returns non-empty list of players")
    public void executeGetAllPlayersRequestAndCheckExistingPlayersListIsReturned() {
        final Response response = httpClient.getAllPlayers()
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(notNullValue())
                .body("players", notNullValue())
                .body("players", hasItems())
                .extract()
                .response();

        final List<PlayerItem> players = response.jsonPath().getList("players", PlayerItem.class);
        assertTrue(players.size() > 0);
    }

    @Description("Test that attempt to get all players list via wrong endpoint leads to the error with 404 or 405 status code.")
    @Test(dataProvider = "wrongUrisForGetAllPlayers", dataProviderClass = TestDataProviders.class,
    description = "Check that sending GET request to wrong URI in order to obtain a list of players " +
            "leads to 404/405 status code")
    public void executeGetAllPlayersRequestWithWrongEndpoint(final String wrongUri) {
        final String endpoint = BASE_URL + wrongUri;
        log.info("Executing GET request to {}", endpoint);

        final Response response = given().when().get(endpoint);
        log.info("Obtained response:\n{}", response.asPrettyString());

        response.then()
                .assertThat()
                .statusCode(in(List.of(404, 405)));
    }
}
