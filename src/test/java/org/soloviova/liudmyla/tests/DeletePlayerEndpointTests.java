package org.soloviova.liudmyla.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.http.ContentType;
import lombok.val;
import org.soloviova.liudmyla.entities.Player;
import org.soloviova.liudmyla.testdata.TestDataProviders;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Contains tests related to Delete Player operations
 *
 * @author Liudmyla Soloviova
 */
@Feature("Delete Player endpoint tests at DELETE /player/delete/{editor}")
public class DeletePlayerEndpointTests extends PlayerTestBase {

    @Description("Test that the supervisor can delete any admin and any user, an admin can delete any user.")
    @Test(dataProvider = "playersWithEditorRoleForDeletion", dataProviderClass = TestDataProviders.class,
    description = "Check that the supervisor can delete admins and users, and that an admin can delete users.")
    public void testThatAdminCanDeleteUserAndSupervisorCanDeleteBothAdminAndUser(final Player player,
                                                                                 final String editorRole) {
        final String editor = editorRole.equals("admin") ? adminLogin : supervisorLogin;
        final Player createdPlayer = createPlayerSafely(player, editor)
                .then()
                .statusCode(in(List.of(200, 201)))
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .extract()
                .as(Player.class);
        checkIfPlayerIsAvailableInAllPlayersList(createdPlayer.getId(), true);

        final Integer playerId = createdPlayer.getId();
        deletePlayerSafely(playerId, editor)
                .then()
                .statusCode(in(List.of(200, 204)));

        checkIfPlayerIsAvailableInAllPlayersList(createdPlayer.getId(), false);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Description("Test that one user cannot delete another user, and one admin cannot delete another admin.")
    @Test(dataProvider = "twoDifferentPlayers", dataProviderClass = TestDataProviders.class,
    description = "Check that one user can't delete another user and one admin can't delete another admin.")
    public void testThatOnePlayerWithUserOrAdminRoleCannotDeleteAnotherPlayer(final Player player1,
                                                                              final Player player2) {
        final Player createdPlayer1 = createPlayerSafely(player1, supervisorLogin)
                .then()
                .statusCode(in(List.of(200, 201)))
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .extract()
                .as(Player.class);
        final Integer firstPlayerId = createdPlayer1.getId();
        checkIfPlayerIsAvailableInAllPlayersList(firstPlayerId, true);

        final Player createdPlayer2 = createPlayerSafely(player2, supervisorLogin)
                .then()
                .statusCode(in(List.of(200, 201)))
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .extract()
                .as(Player.class);
        final Integer secondPlayerId = createdPlayer2.getId();
        checkIfPlayerIsAvailableInAllPlayersList(secondPlayerId, true);

        final String secondPlayerLogin = getPlayerLogin(secondPlayerId);
        deletePlayerSafely(firstPlayerId, secondPlayerLogin)
                .then()
                .statusCode(403);

        checkIfPlayerIsAvailableInAllPlayersList(firstPlayerId, true);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Description("Test that an admin cannot delete be deleted by him/herself.")
    @Test(dataProvider = "oneAdmin", dataProviderClass = TestDataProviders.class,
            description = "Check that an admin cannot delete himself.")
    public void testThatAdminCannotDeleteHimself(final Player customAdmin) {
        val customAdminId = createPlayerSafely(customAdmin, supervisorLogin)
                .then()
                .statusCode(in(List.of(200, 201)))
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .extract()
                .as(Player.class)
                .getId();
        checkIfPlayerIsAvailableInAllPlayersList(customAdminId, true);

        deletePlayerSafely(customAdminId, customAdmin.getLogin())
                .then()
                .statusCode(403);
        checkIfPlayerIsAvailableInAllPlayersList(customAdminId, true);
    }

    @Description("Test that the supervisor cannot be deleted by any admin.")
    @Test(description = "Check that the supervisor cannot be deleted by admins.")
    public void testThatSupervisorCannotBeDeletedByAdmin() {
        httpClient.deletePlayer(supervisorId, adminLogin)
                .then()
                .statusCode(403);

        checkIfPlayerIsAvailableInAllPlayersList(supervisorId, true);
    }

    @Description("Test that the supervisor cannot be deleted by himself.")
    @Test(description = "Check that the supervisor cannot be deleted by himself.")
    public void testThatSupervisorCannotBeDeletedByHimself() {
        httpClient.deletePlayer(supervisorId, supervisorLogin)
                .then()
                .statusCode(403);

        checkIfPlayerIsAvailableInAllPlayersList(supervisorId, true);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Description("Test that a user cannot be deleted by him/herself.")
    @Test(dataProvider = "oneUser", dataProviderClass = TestDataProviders.class,
    description = "Check that a user cannot delete himself")
    public void testThatUserCannotDeleteHimself(final Player user) {
        val userId = createPlayerSafely(user, adminLogin)
                .then()
                .statusCode(in(List.of(200, 201)))
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .extract()
                .as(Player.class)
                .getId();
        checkIfPlayerIsAvailableInAllPlayersList(userId, true);

        deletePlayerSafely(userId, user.getLogin())
                .then()
                .statusCode(403);
        checkIfPlayerIsAvailableInAllPlayersList(userId, true);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Description("Test that a user cannot delete an admin.")
    @Test(dataProvider = "userAndAdmin", dataProviderClass = TestDataProviders.class,
    description = "Check that a user cannot delete admins.")
    public void testThatUserCannotDeleteAdmin(final Player user,
                                              final Player admin) {
        val userId = createPlayerSafely(user, adminLogin)
                .then()
                .statusCode(in(List.of(200, 201)))
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .extract()
                .as(Player.class)
                .getId();
        checkIfPlayerIsAvailableInAllPlayersList(userId, true);

        val adminId = createPlayerSafely(admin, supervisorLogin)
                .then()
                .statusCode(in(List.of(200, 201)))
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .extract()
                .as(Player.class)
                .getId();
        checkIfPlayerIsAvailableInAllPlayersList(adminId, true);

        deletePlayerSafely(adminId, user.getLogin())
                .then()
                .statusCode(403);
        checkIfPlayerIsAvailableInAllPlayersList(adminId, true);
    }

    @Description("Test that a user cannot delete the supervisor.")
    @Test(dataProvider = "oneUser", dataProviderClass = TestDataProviders.class,
    description = "Check that a user cannot delete the supervisor.")
    public void testThatUserCannotDeleteSupervisor(final Player user) {
        val userId = createPlayerSafely(user, supervisorLogin)
                .then()
                .statusCode(in(List.of(200, 201)))
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .extract()
                .as(Player.class)
                .getId();
        checkIfPlayerIsAvailableInAllPlayersList(userId, true);

        val supervisorId = getPlayerIdByLogin(supervisorLogin);
        deletePlayerSafely(supervisorId, user.getLogin())
                .then()
                .statusCode(403);

        checkIfPlayerIsAvailableInAllPlayersList(supervisorId, true);
    }
}
