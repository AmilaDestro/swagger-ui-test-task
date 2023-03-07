package org.soloviova.liudmyla.testdata;

import org.soloviova.liudmyla.entities.Player;
import org.testng.annotations.DataProvider;

public class TestDataProviders {

    @DataProvider
    private static Object[][] wrongUrisForGetAllPlayers() {
        return new Object[][]{
                {"/get/all-players"},
                {"/get"}
        };
    }

    @DataProvider
    public static Object[][] wrongPlayerIds() {
        return new Object[][]{
                {0},
                {-1},
                {null}
        };
    }

    @DataProvider
    public static Object[][] validPlayersToCreateWithEditorRole() {
        return new Object[][]{
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

    @DataProvider
    public static Object[][] twoUsersToCreateOneByOne() {
        return new Object[][]{
                {
                        Player.builder()
                                .login("bob1997")
                                .password("bgheyurg6HW&")
                                .age(25)
                                .role("user")
                                .gender("male")
                                .screenName("BobRoberts")
                                .build(),
                        Player.builder()
                                .login("alan_morgan")
                                .password("9gnutUtfv")
                                .age(27)
                                .role("user")
                                .gender("male")
                                .screenName("Alan_Morgan")
                                .build()
                }
        };
    }

    @DataProvider
    public static Object[][] usersBeyondAllowedAge() {
        return new Object[][] {
                {
                    Player.builder()
                            .login("anonym_user2010")
                            .password("fbrhwuui2")
                            .screenName("Anonymus_10")
                            .age(12)
                            .role("user")
                            .gender("male")
                            .build()
                },
                {
                    Player.builder()
                            .login("jane_doe")
                            .password("qwertyuiop10")
                            .screenName("Jane_Doe_player")
                            .age(62)
                            .gender("female")
                            .role("user")
                            .build()
                }
        };
    }

    @DataProvider
    public static Object[][] twoUsersWithTheSameLogin() {
        return new Object[][] {
                {
                    Player.builder()
                            .login("Winner")
                            .password("123password456")
                            .gender("male")
                            .age(26)
                            .screenName("WINNER")
                            .role("user")
                            .build(),
                        Player.builder()
                                .login("Winner")
                                .password("asdfgytrewq6")
                                .gender("female")
                                .age(24)
                                .screenName("Ashley_Winner")
                                .role("user")
                                .build()
                }
        };
    }

    @DataProvider
    public static Object[][] twoUsersWithTheSameScreenName() {
        return new Object[][] {
                {
                        Player.builder()
                                .login("anne_clarke")
                                .password("vbwhubvuYTVi")
                                .gender("female")
                                .age(31)
                                .screenName("Phoenix")
                                .role("user")
                                .build(),
                        Player.builder()
                                .login("Will_Lloyd_Phoenix")
                                .password("UVTYdfnuq6")
                                .gender("male")
                                .age(36)
                                .screenName("Phoenix")
                                .role("user")
                                .build()
                }
        };
    }

    @DataProvider
    public static Object[][] twoDifferentPlayers() {
        return new Object[][] {
                {
                    Player.builder()
                            .login("oliver89")
                            .password("YUBbhydg54d91")
                            .screenName("Oliver_Ryd")
                            .gender("male")
                            .age(34)
                            .role("user")
                            .build(),
                        Player.builder()
                                .login("james_frost")
                                .password("nvbui4u74")
                                .screenName("James Frost")
                                .gender("male")
                                .age(37)
                                .role("user")
                                .build()
                },
                {
                        Player.builder()
                                .login("HelenaJ")
                                .password("bfur728ffbul")
                                .screenName("Helena_Jones")
                                .gender("female")
                                .age(25)
                                .role("admin")
                                .build(),
                        Player.builder()
                                .login("emilia_the_player")
                                .password("vn43n8G&GFf")
                                .screenName("Emilia_Holmes")
                                .gender("female")
                                .age(33)
                                .role("admin")
                                .build()
                }
        };
    }
}
