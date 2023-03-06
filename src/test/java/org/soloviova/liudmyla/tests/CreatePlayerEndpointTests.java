package org.soloviova.liudmyla.tests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.val;
import org.soloviova.liudmyla.entities.Player;
import org.soloviova.liudmyla.testdata.TestDataProviders;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.Matchers.in;
import static org.testng.Assert.assertEquals;

/**
 * Contains tests related to Create Player operations
 *
 * @author Liudmyla Soloviova
 */
public class CreatePlayerEndpointTests extends PlayerTestBase {

    @Test(dataProvider = "validPlayersToCreateWithEditor", dataProviderClass = TestDataProviders.class)
    public void createValidNewPlayerByAdminOrSupervisor(final Player playerToCreate,
                                                        final String editor) {


        final Player createdPlayer = createPlayerSafely(playerToCreate, editor)
                .then()
                .statusCode(in(List.of(200, 201)))
                .contentType(ContentType.JSON)
                .extract()
                .response()
                .as(Player.class);

        checkIfPlayerIsAvailableInAllPlayersList(createdPlayer, true);

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

    @Test(dataProvider = "twoUsersToCreateOneByOne", dataProviderClass = TestDataProviders.class)
    public void createNewUserByAnotherUser(final Player player1,
                                           final Player player2) {
        final Response givenResponse1 = createPlayerSafely(player1, "supervisor");
        final Player createdPlayer1 = givenResponse1.then()
                .statusCode(in(List.of(200, 201)))
                .contentType(ContentType.JSON)
                .extract()
                .response()
                .jsonPath()
                .getObject("", Player.class);

        checkIfPlayerIsAvailableInAllPlayersList(createdPlayer1, true);

        createPlayerSafely(player2, "user")
                .then()
                .statusCode(403);
    }

    @Test
    public void createNewSupervisorByExistingSupervisor() {
        val supervisorToCreate = Player.builder()
                .screenName("Supervisor_sister")
                .age(34)
                .gender("female")
                .login("anotherSupervisor")
                .password("QwertY987")
                .role("supervisor")
                .build();
        createPlayerSafely(supervisorToCreate, "supervisor")
                .then()
                .statusCode(403);
    }

    @Test(dataProvider = "usersBeyondAllowedAge", dataProviderClass = TestDataProviders.class)
    public void createNewUsersBeyondAllowedAge(final Player player) {
        createPlayerSafely(player, "supervisor")
                .then()
                .statusCode(403);
    }

    @Test(dataProvider = "twoUsersWithTheSameLogin", dataProviderClass = TestDataProviders.class)
    public void createNewUserWithTheSameLogin(final Player player1,
                                              final Player player2) {
        final Integer firstPlayerId = createPlayerSafely(player1, "supervisor")
                .then()
                .statusCode(in(List.of(200, 201)))
                .contentType(ContentType.JSON)
                .extract()
                .as(Player.class)
                .getId();
        final Player createdPlayer1 = httpClient.getPlayerByIdSuppressRequestException(firstPlayerId);

        createPlayerSafely(player2, "supervisor")
                .then()
                .statusCode(403);

        final Player player1AfterCreationOfPlayer2 = httpClient.getPlayerByIdSuppressRequestException(firstPlayerId);
        assertEquals(player1AfterCreationOfPlayer2, createdPlayer1);
    }

    @Test(dataProvider = "twoUsersWithTheSameScreenName", dataProviderClass = TestDataProviders.class)
    public void createNewUserWithTheSameScreenName(final Player player1,
                                                   final Player player2) {
        final Integer firstPlayerId = createPlayerSafely(player1, "supervisor")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(Player.class)
                .getId();
        final Player firstPlayerAfterCreation = httpClient.getPlayerByIdSuppressRequestException(firstPlayerId);

        createPlayerSafely(player2, "supervisor")
                .then()
                .statusCode(403);

        final Player firstPlayerAfterSecondPlayerCreation = httpClient.getPlayerByIdSuppressRequestException(firstPlayerId);
        assertEquals(firstPlayerAfterSecondPlayerCreation, firstPlayerAfterCreation);
    }
}
