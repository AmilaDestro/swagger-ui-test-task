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

    @Test(dataProvider = "playersWithEditorRoleForCreation", dataProviderClass = TestDataProviders.class,
            description = "Check that supervisor can create both admins and users, and that admin can create " +
                    "admins and users too")
    public void testThatSupervisorAndAdminCanCreateAdminsAndUsers(final Player playerToCreate,
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

    @Test(dataProvider = "twoUsersToCreateOneByOne", dataProviderClass = TestDataProviders.class,
            description = "Check that one user can't create another user")
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

    @Test(dataProvider = "customSupervisor", dataProviderClass = TestDataProviders.class,
            description = "Check that a new supervisor cannot be created even by existing supervisor")
    public void testThatSupervisorCannotCreateAnotherSupervisor(final Player customSupervisor) {
        createPlayerSafely(customSupervisor, supervisorLogin)
                .then()
                .statusCode(403);
        checkIfPlayerIsAvailableInAllPlayersList(customSupervisor.getLogin(), false);
    }

    @Test(dataProvider = "customSupervisor", dataProviderClass = TestDataProviders.class,
            description = "Check that admin cannot create a new supervisor")
    public void testThatAdminCannotCreateSupervisor(final Player customSupervisor) {
        createPlayerSafely(customSupervisor, adminLogin)
                .then()
                .statusCode(403);
        checkIfPlayerIsAvailableInAllPlayersList(customSupervisor.getLogin(), false);
    }

    @Test(dataProvider = "usersBeyondAllowedAge", dataProviderClass = TestDataProviders.class,
    description = "Check that new users/admins cannot be created if their age is less than 16 and more than 60")
    public void testThatNewUsersBeyondAllowedAgeCannotBeCreated(final Player player) {
        createPlayerSafely(player, supervisorLogin)
                .then()
                .statusCode(403);
        checkIfPlayerIsAvailableInAllPlayersList(player.getLogin(), false);
    }

    @Test(dataProvider = "twoUsersWithTheSameLogin", dataProviderClass = TestDataProviders.class,
    description = "Check that 2 users with the same login cannot be created")
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

    @Test(dataProvider = "twoUsersWithTheSameScreenName", dataProviderClass = TestDataProviders.class,
    description = "Check that 2 users with the same screenName cannot be created")
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

    @Test(dataProvider = "userAndSupervisorThenUserAndAdmin", dataProviderClass = TestDataProviders.class,
    description = "Check that a user cannot create neither new supervisor nor admin")
    public void testThatUserCannotCreateAdminOrSupervisor(final Player user,
                                                          final Player superAdmin) {
        val userId = createPlayerSafely(user, supervisorLogin)
                .then()
                .statusCode(in(List.of(200, 201)))
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .extract()
                .as(Player.class)
                .getId();
        checkIfPlayerIsAvailableInAllPlayersList(userId, true);

        createPlayerSafely(superAdmin, user.getLogin())
                .then()
                .statusCode(403);
        checkIfPlayerIsAvailableInAllPlayersList(superAdmin.getLogin(), false);
    }
}
