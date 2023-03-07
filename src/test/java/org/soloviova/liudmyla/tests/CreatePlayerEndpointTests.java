package org.soloviova.liudmyla.tests;

import io.restassured.http.ContentType;
import lombok.val;
import org.soloviova.liudmyla.entities.Player;
import org.soloviova.liudmyla.testdata.TestDataProviders;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.assertEquals;

/**
 * Contains tests related to Create Player operations
 *
 * @author Liudmyla Soloviova
 */
public class CreatePlayerEndpointTests extends PlayerTestBase {

    @Test(dataProvider = "validPlayersToCreateWithEditorRole", dataProviderClass = TestDataProviders.class)
    public void testThatAdminCanCreateUserAndSupervisorCanCreateAdminAndCheckResponseBody(final Player playerToCreate,
                                                                                          final String editorRole) {
        final String editor = editorRole.equals("admin") ? adminLogin : supervisorLogin;
        final Player createdPlayerInResponse = createPlayerSafely(playerToCreate, editor)
                .then()
                .statusCode(in(List.of(200, 201)))
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .extract()
                .response()
                .as(Player.class);

        checkIfPlayerIsAvailableInAllPlayersList(createdPlayerInResponse.getId(), true);

        assertEquals(createdPlayerInResponse.getLogin(), playerToCreate.getLogin(),
                "Created Player's 'login' doesn't match");
        assertEquals(createdPlayerInResponse.getScreenName(), playerToCreate.getScreenName(),
                "Created Player's 'screenName' doesn't match");
        assertEquals(createdPlayerInResponse.getGender(), playerToCreate.getGender(),
                "Created Player's 'gender' doesn't match");
        assertEquals(createdPlayerInResponse.getAge(), playerToCreate.getAge(),
                "Created Player's 'age' doesn't match");
        assertEquals(createdPlayerInResponse.getRole(), playerToCreate.getRole(),
                "Created Player's 'role' doesn't match");
    }

    @Test(dataProvider = "twoUsersToCreateOneByOne", dataProviderClass = TestDataProviders.class)
    public void testThatUserCannotBeCreatedByAnotherUser(final Player player1,
                                                         final Player player2) {
        final Player createdPlayer1 = createPlayerSafely(player1, supervisorLogin)
                .then()
                .statusCode(in(List.of(200, 201)))
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .extract()
                .response()
                .as(Player.class);

        checkIfPlayerIsAvailableInAllPlayersList(createdPlayer1.getId(), true);

        final String player1Login = getPlayerLogin(createdPlayer1.getId());
        createPlayerSafely(player2, player1Login)
                .then()
                .statusCode(403);
        checkIfPlayerIsAvailableInAllPlayersList(player2.getLogin(), false);
    }

    @Test
    public void testThatAdminCanCreateAnotherAdmin() {
        final Player secondAdmin = Player.builder()
                .login("testAdmin2")
                .age(30)
                .gender("male")
                .screenName("Test_Admin_2")
                .password("9876fvry3ufv")
                .role("admin")
                .build();

        final Integer secondAdminId = createPlayerSafely(secondAdmin, adminLogin)
                .then()
                .statusCode(in(List.of(200, 201)))
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .extract()
                .as(Player.class)
                .getId();
        checkIfPlayerIsAvailableInAllPlayersList(secondAdminId, true);
    }

    @Test
    public void testThatSupervisorCannotCreateAnotherSupervisor() {
        val supervisorToCreate = Player.builder()
                .screenName("Supervisor_sister")
                .age(34)
                .gender("female")
                .login("anotherSupervisor")
                .password("QwertY987")
                .role("supervisor")
                .build();
        createPlayerSafely(supervisorToCreate, supervisorLogin)
                .then()
                .statusCode(403);
        checkIfPlayerIsAvailableInAllPlayersList(supervisorToCreate.getLogin(), false);
    }

    @Test(dataProvider = "usersBeyondAllowedAge", dataProviderClass = TestDataProviders.class)
    public void testThatNewUsersBeyondAllowedAgeCannotBeCreated(final Player player) {
        createPlayerSafely(player, supervisorLogin)
                .then()
                .statusCode(403);
        checkIfPlayerIsAvailableInAllPlayersList(player.getLogin(), false);
    }

    @Test(dataProvider = "twoUsersWithTheSameLogin", dataProviderClass = TestDataProviders.class)
    public void testThatTwoUsersWithTheSameLoginCannotBeCreated(final Player player1,
                                                                final Player player2) {
        final Integer firstPlayerId = createPlayerSafely(player1, adminLogin)
                .then()
                .statusCode(in(List.of(200, 201)))
                .contentType(ContentType.JSON)
                .extract()
                .as(Player.class)
                .getId();
        final Player createdPlayer1 = httpClient.getPlayerByIdSuppressRequestException(firstPlayerId);

        createPlayerSafely(player2, adminLogin)
                .then()
                .statusCode(403);
        checkIfPlayerIsAvailableInAllPlayersList(player2.getLogin(), false);

        final Player player1AfterCreationOfPlayer2 = httpClient.getPlayerByIdSuppressRequestException(firstPlayerId);
        assertEquals(player1AfterCreationOfPlayer2, createdPlayer1);
    }

    @Test(dataProvider = "twoUsersWithTheSameScreenName", dataProviderClass = TestDataProviders.class)
    public void testThatTwoUsersWithTheSameScreenNameCannotBeCreated(final Player player1,
                                                                     final Player player2) {
        final Integer firstPlayerId = createPlayerSafely(player1, supervisorLogin)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(Player.class)
                .getId();
        final Player firstPlayerAfterCreation = httpClient.getPlayerByIdSuppressRequestException(firstPlayerId);

        createPlayerSafely(player2, supervisorLogin)
                .then()
                .statusCode(403);
        checkIfPlayerIsAvailableInAllPlayersList(player2.getLogin(), false);

        final Player firstPlayerAfterSecondPlayerCreation = httpClient.getPlayerByIdSuppressRequestException(firstPlayerId);
        assertEquals(firstPlayerAfterSecondPlayerCreation, firstPlayerAfterCreation);
    }
}
