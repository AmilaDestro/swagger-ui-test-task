package org.soloviova.liudmyla.testdata;

import org.soloviova.liudmyla.entities.Player;
import org.testng.annotations.DataProvider;

public class TestDataProviders {

    @DataProvider
    private static Object[][] wrongUrisForGetAllPlayers() {
        return new Object[][] {
                {"/get/all-players"},
                {"/get"}
        };
    }

    @DataProvider
    public static Object[][] wrongPlayerIds() {
        return new Object[][] {
                {0},
                {-1},
                {null}
        };
    }

    @DataProvider
    public static Object[][] validPlayersToCreateWithEditor() {
        return new Object[][] {
                {Player.builder()
                        .age(31)
                        .role("user")
                        .gender("male")
                        .login("mr_smith")
                        .password("Password0123")
                        .screenName("John_Smith")
                        .build(),
                        "admin"
                },
                {
                    Player.builder()
                            .age(30)
                            .role("admin")
                            .gender("female")
                            .login("jane_smith")
                            .password("1Passw0rd567")
                            .screenName("Jane_Smith")
                            .build(),
                        "supervisor"
                }
        };
    }
}
