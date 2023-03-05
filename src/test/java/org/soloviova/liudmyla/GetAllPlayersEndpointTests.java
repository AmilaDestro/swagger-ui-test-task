package org.soloviova.liudmyla;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.assertEquals;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.soloviova.liudmyla.entities.PlayerItem;
import org.testng.annotations.Test;

import java.util.List;

/**
 * This class contains tests related to getAllPlayers controller
 *
 * @author Liudmyla Soloviova
 */
@Slf4j
public class GetAllPlayersEndpointTests extends TestBase {

    @Test
    public void executeGetAllPlayersRequestAndCheckExistingPlayersListIsReturned() {
        log.info("Trying to obtain a list of all registered players");
        final String endpoint = BASE_URL + "/get/all";
        log.info("Executing GET request to {}", endpoint);

        final Response response = given()
                .when()
                .get(endpoint)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(notNullValue())
                .body("players", notNullValue())
                .body("players", hasItems())
                .extract()
                .response();

        log.info("Obtained response:\n{}", response.asPrettyString());

        final List<PlayerItem> players = response.jsonPath().getList("players", PlayerItem.class);
        verifyThatAllRegisteredPlayersHaveAllowedAge(players);
        verifyGenderOfRegisteredPlayers(players);
    }

    private void verifyThatAllRegisteredPlayersHaveAllowedAge(final List<PlayerItem> registeredPlayers) {
        assertEquals(registeredPlayers.stream()
                                      .filter(player -> {
                                          final int age = player.getAge();
                                          return age > 16 && age < 60;
                                      })
                                      .count(),
                     registeredPlayers.size(),
                "All players should be older than 16 and younger than 60 years old");
    }

    private void verifyGenderOfRegisteredPlayers(final List<PlayerItem> registeredPlayers) {
        assertEquals(registeredPlayers.stream()
                        .filter(player -> {
                            final String gender = player.getGender();
                            return gender.equals("male") || gender.equals("female");
                        })
                        .count(),
                registeredPlayers.size(),
                "User's gender can only be: ‘male’ or ‘female’");
    }
}
