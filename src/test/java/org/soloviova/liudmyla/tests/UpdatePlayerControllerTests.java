package org.soloviova.liudmyla.tests;

import io.restassured.http.ContentType;
import lombok.val;
import org.soloviova.liudmyla.entities.Player;
import org.soloviova.liudmyla.testdata.TestDataProviders;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

public class UpdatePlayerControllerTests extends PlayerTestBase {

    @Test(dataProvider = "userThenAdminPlayers", dataProviderClass = TestDataProviders.class,
            description = "Check that the supervisor can edit users and admins")
    public void testThatSupervisorCanEditPlayerWithAdminAndUserRoles(final Player player) {
        val playerId = createPlayerSafely(player, supervisorLogin)
                .then()
                .statusCode(in(List.of(200, 201)))
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .extract()
                .as(Player.class)
                .getId();
        checkIfPlayerIsAvailableInAllPlayersList(playerId, true);

        val playerAfterCreation = httpClient.getPlayerByIdSuppressRequestException(playerId);

        val playerToUpdate = Player.builder()
                .screenName(playerAfterCreation.getScreenName() + "_UPD")
                .age(25)
                .build();
        val updatedPlayer = httpClient.updatePlayer(playerId, supervisorLogin, playerToUpdate)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(Player.class);

        checkIfPlayerIsAvailableInAllPlayersList(playerId, true);

        assertEquals(updatedPlayer.getId(), playerId, "Player's id after update doesn't match");
        assertEquals(updatedPlayer.getLogin(), playerAfterCreation.getLogin(),
                "Player's login after update doesn't match");
        assertEquals(updatedPlayer.getRole(), playerAfterCreation.getRole(),
                "Player's role after update doesn't match");
        assertEquals(updatedPlayer.getGender(), playerAfterCreation.getGender(),
                "Player's role after update doesn't match");
        assertNotEquals(updatedPlayer.getAge(), playerAfterCreation.getAge(), "Player's age was not changed");
        assertNotEquals(updatedPlayer.getScreenName(), playerAfterCreation.getScreenName(),
                "Player's screenName was not changed");
        assertEquals(updatedPlayer.getAge(), playerToUpdate.getAge(), "Player's age was not properly changed");
        assertEquals(updatedPlayer.getScreenName(), playerToUpdate.getScreenName(),
                "Player's screenName was not properly changed");
    }

    @Test(description = "Check that the supervisor can update himself")
    public void testThatSupervisorCanUpdateHimself() {
        val supervisorId = getPlayerIdByLogin(supervisorLogin);
        val supervisorBeforeUpdate = httpClient.getPlayerByIdSuppressRequestException(supervisorId);

        val supervisorToEdit = Player.builder()
                .screenName(supervisorBeforeUpdate.getScreenName() + "_upd")
                .build();
        val updatedSupervisor = httpClient.updatePlayer(supervisorId, supervisorLogin, supervisorToEdit)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("screenName", equalTo(supervisorToEdit.getScreenName()))
                .body("login", equalTo(supervisorLogin))
                .body("age", equalTo(supervisorBeforeUpdate.getAge()))
                .body("role", equalTo(supervisorBeforeUpdate.getRole()))
                .body("gender", equalTo(supervisorBeforeUpdate.getGender()))
                .extract()
                .as(Player.class);
        assertNotEquals(updatedSupervisor, supervisorBeforeUpdate, "Supervisor was not changed");
    }

    @Test(dataProvider = "oneUser", dataProviderClass = TestDataProviders.class,
            description = "Check that an admin can update users")
    public void testThatAdminCanUpdateUsers(final Player user) {
        val userId = createPlayerSafely(user, adminLogin)
                .then()
                .statusCode(in(List.of(200, 201)))
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .extract()
                .as(Player.class)
                .getId();
        checkIfPlayerIsAvailableInAllPlayersList(userId, true);

        val userBeforeUpdate = httpClient.getPlayerByIdSuppressRequestException(userId);
        val userToUpdate = Player.builder()
                .age(35)
                .login(userBeforeUpdate.getLogin().toUpperCase())
                .build();

        final Player updatedUser = httpClient.updatePlayer(userId, adminLogin, userToUpdate)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(userId))
                .body("age", equalTo(userToUpdate.getAge()))
                .body("login", equalTo(userToUpdate.getLogin()))
                .body("gender", equalTo(userBeforeUpdate.getGender()))
                .body("role", equalTo(userBeforeUpdate.getRole()))
                .body("screenName", equalTo(userBeforeUpdate.getScreenName()))
                .extract()
                .as(Player.class);
        assertNotEquals(updatedUser, userBeforeUpdate, "User was not changed");
    }

    @Test(dataProvider = "userThenAdminPlayers", dataProviderClass = TestDataProviders.class,
            description = "Check that an admin can update himself and a user can update himself")
    public void testThatAdminCanUpdateHimselfAndUserCanUpdateHimself(final Player player) {
        val playerId = createPlayerSafely(player, supervisorLogin)
                .then()
                .statusCode(in(List.of(200, 201)))
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .extract()
                .as(Player.class)
                .getId();
        checkIfPlayerIsAvailableInAllPlayersList(playerId, true);

        val playerAfterCreation = httpClient.getPlayerByIdSuppressRequestException(playerId);
        val playerToUpdate = Player.builder()
                .screenName(playerAfterCreation.getScreenName().toUpperCase() + "_UPD")
                .password("BTGccenf43yf4fhf")
                .build();

        val updatedPlayerResponse = httpClient.updatePlayer(playerId, playerAfterCreation.getLogin(), playerToUpdate)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(playerId))
                .extract()
                .as(Player.class);

        assertNotEquals(updatedPlayerResponse.getScreenName(), playerAfterCreation.getScreenName(),
                "Player's screenName response doesn't contain updated value");
        assertNotEquals(updatedPlayerResponse.getPassword(), playerAfterCreation.getPassword(),
                "Player's password response doesn't contain updated value");

        assertEquals(updatedPlayerResponse.getLogin(), playerAfterCreation.getLogin(),
                "Player's login in response shouldn't have been changed");
        assertEquals(updatedPlayerResponse.getAge(), playerAfterCreation.getAge(),
                "Player's age in response shouldn't have been changed");
        assertEquals(updatedPlayerResponse.getGender(), playerAfterCreation.getGender(),
                "Player's gender in response shouldn't have been changed");
        assertEquals(updatedPlayerResponse.getRole(), playerAfterCreation.getRole(),
                "Player's role in response shouldn't have been changed");

        val playerAfterUpdate = httpClient.getPlayerByIdSuppressRequestException(playerId);
        assertNotEquals(playerAfterUpdate, playerAfterCreation, "Player was not updated");
    }

    @Test(dataProvider = "twoDifferentPlayers", dataProviderClass = TestDataProviders.class,
            description = "Check that a user cannot update other users and an admin cannot update other admins")
    public void testThatUserCannotUpdateOtherUsersAndAdminCannotUpdateOtherAdmins(final Player player1,
                                                                                  final Player player2) {
        val playerId1 = createPlayerSafely(player1, adminLogin)
                .then()
                .statusCode(in(List.of(200, 201)))
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .extract()
                .as(Player.class)
                .getId();
        checkIfPlayerIsAvailableInAllPlayersList(playerId1, true);

        val playerId2 = createPlayerSafely(player2, adminLogin)
                .then()
                .statusCode(in(List.of(200, 201)))
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .extract()
                .as(Player.class)
                .getId();
        checkIfPlayerIsAvailableInAllPlayersList(playerId2, true);

        val player1AfterCreation = httpClient.getPlayerByIdSuppressRequestException(playerId1);
        val player1ToUpdate = Player.builder()
                .screenName(player1.getScreenName() + "_upd_1")
                .build();

        httpClient.updatePlayer(playerId1, player2.getLogin(), player1ToUpdate)
                .then()
                .statusCode(403);
        assertEquals(httpClient.getPlayerByIdSuppressRequestException(playerId1), player1AfterCreation,
                "Player 1 should not have been updated");
    }

    @Test
    public void testThatAdminCannotUpdateSupervisor() {
        val supervisor = httpClient.getPlayerByIdSuppressRequestException(supervisorId);
        val toUpdate = Player.builder()
                .login(supervisor.getLogin().toUpperCase())
                .screenName(supervisor.getScreenName() + " updated")
                .build();

        httpClient.updatePlayer(supervisorId, adminLogin, toUpdate)
                .then()
                .statusCode(403);

        val supervisorAfterUpdate = httpClient.getPlayerByIdSuppressRequestException(supervisorId);
        assertEquals(supervisorAfterUpdate, supervisor, "Supervisor should not have been updated");
    }

    @Test(dataProvider = "oneUser", dataProviderClass = TestDataProviders.class)
    public void testThatUserCannotUpdateAdmin(final Player user) {
        val userId = createPlayerSafely(user, supervisorLogin)
                .then()
                .statusCode(in(List.of(200, 201)))
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .extract()
                .as(Player.class)
                .getId();
        checkIfPlayerIsAvailableInAllPlayersList(userId, true);

        val adminId = getPlayerIdByLogin(adminLogin);
        val admin = httpClient.getPlayerByIdSuppressRequestException(adminId);
        val toUpdate = Player.builder()
                .screenName(admin.getScreenName().toUpperCase() + "_UPD")
                .build();

        httpClient.updatePlayer(adminId, user.getLogin(), toUpdate)
                .then()
                .statusCode(403);
        val adminAfterUpdate = httpClient.getPlayerByIdSuppressRequestException(adminId);
        assertEquals(adminAfterUpdate, admin, "Admin should not have been updated");
    }

    @Test(dataProvider = "oneUser", dataProviderClass = TestDataProviders.class)
    public void testThatUserCannotUpdateSupervisor(final Player user) {
        val userId = createPlayerSafely(user, supervisorLogin)
                .then()
                .statusCode(in(List.of(200, 201)))
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .extract()
                .as(Player.class)
                .getId();
        checkIfPlayerIsAvailableInAllPlayersList(userId, true);

        val supervisor = httpClient.getPlayerByIdSuppressRequestException(supervisorId);
        val toUpdate = Player.builder()
                .gender("female")
                .age(29)
                .screenName(supervisor.getScreenName().toUpperCase() + "_UPD")
                .build();

        httpClient.updatePlayer(supervisorId, user.getLogin(), toUpdate)
                .then()
                .statusCode(403);
        val supervisorAfterUpdate = httpClient.getPlayerByIdSuppressRequestException(supervisorId);
        assertEquals(supervisorAfterUpdate, supervisor, "Supervisor should not have been updated");
    }
}
