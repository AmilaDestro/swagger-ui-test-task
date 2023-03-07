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
}
