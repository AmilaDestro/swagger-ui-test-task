package org.soloviova.liudmyla.tests;

import io.restassured.http.ContentType;
import lombok.val;
import org.soloviova.liudmyla.entities.Player;
import org.soloviova.liudmyla.testdata.TestDataProviders;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.notNullValue;

public class DeletePlayerEndpointTests extends PlayerTestBase {

    @Test(dataProvider = "validPlayersToCreateWithEditorRole", dataProviderClass = TestDataProviders.class)
    public void testThatAdminCanDeleteUserAndSupervisorCanDeleteAdmin(final Player player,
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

    @Test(dataProvider = "twoDifferentPlayers", dataProviderClass = TestDataProviders.class,
    description = "Checks that one user can't delete another user and one admin can't delete another admin")
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

    @Test
    public void deleteSupervisorByAdmin() {
        val supervisorId = getPlayerIdByLogin(supervisorLogin);
        httpClient.deletePlayer(supervisorId, adminLogin)
                .then()
                .statusCode(403);

        checkIfPlayerIsAvailableInAllPlayersList(supervisorId, true);
    }
}
