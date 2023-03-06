package org.soloviova.liudmyla.tests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.soloviova.liudmyla.entities.Player;
import org.soloviova.liudmyla.testdata.TestDataProviders;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.in;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@Slf4j
public class CreatePlayerEndpointTests extends PlayersControllerTestBase {

    @Test(dataProvider = "validPlayersToCreateWithEditor", dataProviderClass = TestDataProviders.class)
    public void createValidNewPlayerByAdminOrSupervisor(final Player playerToCreate,
                                                        final String editor) {
        log.info("Creating a new Player: {}", playerToCreate.toString());

        Response response = given()
                .param("age", playerToCreate.getAge())
                .param("gender", playerToCreate.getGender())
                .param("login", playerToCreate.getLogin())
                .param("password", playerToCreate.getPassword())
                .param("role", playerToCreate.getRole())
                .param("screenName", playerToCreate.getScreenName())
                .get(CREATE_PLAYER_URL, editor)
                .then()
                .statusCode(in(List.of(200, 201)))
                .contentType(ContentType.JSON)
                .extract()
                .response();

        log.info("Obtained response: {}", response.asPrettyString());

        Player createdPlayer = response.jsonPath().getObject("", Player.class);

        assertNotNull(createdPlayer.getId(), "'id' of created Player should not be null");
        playersToDelete.add(createdPlayer.getId());

        assertEquals(createdPlayer.getLogin(), playerToCreate.getLogin(),
                "Created Player's 'login' doesn't match");
        assertEquals(createdPlayer.getScreenName(), playerToCreate.getScreenName(),
                "Created Player's 'screenName' doesn't match");
        assertEquals(createdPlayer.getGender(), playerToCreate.getGender(),
                "Created Player's 'gender' doesn't match");
        assertEquals(createdPlayer.getAge(), playerToCreate.getAge(),
                "Created Player's 'age' doesn't match");
        assertEquals(createdPlayer.getRole(), playerToCreate.getRole(),
                "Created Player's 'role' doesn't match");
    }
}
