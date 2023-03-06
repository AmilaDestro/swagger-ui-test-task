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

    @Test(dataProvider = "validPlayersToCreateWithEditor", dataProviderClass = TestDataProviders.class)
    public void deleteExistingPlayerBySupervisorOrAdmin(final Player player,
                                                        final String editor) {
        final Player createdPlayer = createPlayerSafely(player, "supervisor")
                .then()
                .statusCode(in(List.of(200, 201)))
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .extract()
                .as(Player.class);
        checkIfPlayerIsAvailableInAllPlayersList(createdPlayer, true);

        final Integer playerId = createdPlayer.getId();
        deletePlayerSafely(playerId, editor)
                .then()
                .statusCode(in(List.of(200, 204)));

        checkIfPlayerIsAvailableInAllPlayersList(createdPlayer, false);
    }

    @Test(dataProvider = "twoDifferentPlayers", dataProviderClass = TestDataProviders.class)
    public void deleteExistingPlayerByUser(final Player player) {
        final Player createdPlayer = createPlayerSafely(player, "supervisor")
                .then()
                .statusCode(in(List.of(200, 201)))
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .extract()
                .as(Player.class);
        checkIfPlayerIsAvailableInAllPlayersList(createdPlayer, true);

        final Integer playerId = createdPlayer.getId();
        deletePlayerSafely(playerId, "user")
                .then()
                .statusCode(403);

        checkIfPlayerIsAvailableInAllPlayersList(createdPlayer, true);
    }

    @Test
    public void deleteSupervisorByUser() {
        val supervisorPlayerItem = httpClient.getAllPlayersSuppressRequestException()
                .stream()
                .filter(playerItem ->
                    httpClient.getPlayerByIdSuppressRequestException(playerItem.getId()).getRole().equals("supervisor")
                )
                .findFirst()
                .orElseThrow(() -> new AssertionError("No players with 'supervisor' role were found"));
        final Player supervisor = httpClient.getPlayerByIdSuppressRequestException(supervisorPlayerItem.getId());

        httpClient.deletePlayer(supervisorPlayerItem.getId(), "user")
                .then()
                .statusCode(403);

        checkIfPlayerIsAvailableInAllPlayersList(supervisor, true);
    }
}
