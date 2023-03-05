package org.soloviova.liudmyla.tests;

import io.restassured.RestAssured;
import org.soloviova.liudmyla.entities.PlayerItem;
import org.testng.annotations.BeforeClass;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.notNullValue;

public abstract class PlayersControllerTestBase {
    protected final String BASE_URL = "http://3.68.165.45/player";
    private final String GET_ALL_PLAYERS_URL = BASE_URL + "/get/all";

    @BeforeClass
    public void setupBeforeTests() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
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
}
