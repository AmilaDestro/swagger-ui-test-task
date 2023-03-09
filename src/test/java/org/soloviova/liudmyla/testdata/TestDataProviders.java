package org.soloviova.liudmyla.testdata;

import org.soloviova.liudmyla.entities.Player;
import org.testng.annotations.DataProvider;

import java.util.UUID;

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
    public static Object[][] playersWithEditorRoleForCreation() {
        return new Object[][] {
                {
                    Player.builder()
                            .login(generateRandomString("Adventurer"))
                            .password("VyqvTtY736JJSD")
                            .screenName(generateRandomString("Alice Ashcroft"))
                            .gender("female")
                            .age(24)
                            .role("user")
                            .build(),
                        "admin"
                },
                {
                        Player.builder()
                                .login(generateRandomString("thomas_birne"))
                                .password("uTCcvjew64ejd3")
                                .screenName(generateRandomString("Tom Birne"))
                                .gender("male")
                                .age(29)
                                .role("admin")
                                .build(),
                        "supervisor"
                },
                {
                        Player.builder()
                                .login(generateRandomString("Starkiller99"))
                                .password("nnjcei2gc")
                                .screenName(generateRandomString("Neil_Starkiller"))
                                .gender("male")
                                .age(23)
                                .role("user")
                                .build(),
                        "supervisor"
                },
                {
                        Player.builder()
                                .login(generateRandomString("hanna_epic"))
                                .password("Bhcuweyyvxcuq")
                                .screenName(generateRandomString("Johanna_The_One"))
                                .gender("female")
                                .age(28)
                                .role("admin")
                                .build(),
                        "admin"
                }
        };
    }

    @DataProvider
    public static Object[][] playersWithEditorRoleForDeletion() {
        return new Object[][]{
                {
                    Player.builder()
                        .age(31)
                        .role("user")
                        .gender("male")
                        .login(generateRandomString("mr_smith"))
                        .password("Password0123")
                        .screenName(generateRandomString("John_Smith"))
                        .build(),
                        "admin"
                },
                {
                        Player.builder()
                                .age(30)
                                .role("admin")
                                .gender("female")
                                .login(generateRandomString("jane_smith"))
                                .password("1Passw0rd567")
                                .screenName(generateRandomString("Jane_Smith"))
                                .build(),
                        "supervisor"
                },
                {
                        Player.builder()
                                .age(27)
                                .role("user")
                                .gender("male")
                                .login(generateRandomString("alex_smith"))
                                .password("qwert09Password0123")
                                .screenName(generateRandomString("AleXSmith"))
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
                                .password(generateRandomString("bgheyurg6HW&"))
                                .age(25)
                                .role("user")
                                .gender("male")
                                .screenName(generateRandomString("BobRoberts"))
                                .build(),
                        Player.builder()
                                .login(generateRandomString("alan_morgan"))
                                .password("9gnutUtfv")
                                .age(27)
                                .role("user")
                                .gender("male")
                                .screenName(generateRandomString("Alan_Morgan"))
                                .build()
                }
        };
    }

    @DataProvider
    public static Object[][] usersBeyondAllowedAge() {
        return new Object[][] {
                {
                    Player.builder()
                            .login(generateRandomString("anonym_user2010"))
                            .password("fbrhwuui2")
                            .screenName(generateRandomString("Anonymus_10"))
                            .age(12)
                            .role("user")
                            .gender("male")
                            .build()
                },
                {
                    Player.builder()
                            .login(generateRandomString("jane_doe"))
                            .password("qwertyuiop10")
                            .screenName(generateRandomString("Jane_Doe_player"))
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
                            .login(generateRandomString("oliver89"))
                            .password("YUBbhydg54d91")
                            .screenName(generateRandomString("Oliver_Ryd"))
                            .gender("male")
                            .age(34)
                            .role("user")
                            .build(),
                        Player.builder()
                                .login(generateRandomString("james_frost"))
                                .password("nvbui4u74")
                                .screenName(generateRandomString("James Frost"))
                                .gender("male")
                                .age(37)
                                .role("user")
                                .build()
                },
                {
                        Player.builder()
                                .login(generateRandomString("HelenaJ"))
                                .password("bfur728ffbul")
                                .screenName(generateRandomString("Helena_Jones"))
                                .gender("female")
                                .age(25)
                                .role("admin")
                                .build(),
                        Player.builder()
                                .login(generateRandomString("emilia_the_player"))
                                .password("vn43n8G&GFf")
                                .screenName(generateRandomString("Emilia_Holmes"))
                                .gender("female")
                                .age(33)
                                .role("admin")
                                .build()
                }
        };
    }

    @DataProvider
    public static Object[][] oneUser() {
        return new Object[][] {
                {
                        Player.builder()
                                .login(generateRandomString("richard_bloom"))
                                .screenName(generateRandomString("Richard_Bloom_Jr"))
                                .password("bcry4748ch")
                                .age(34)
                                .gender("male")
                                .role("user")
                                .build()
                }
        };
    }

    @DataProvider
    public static Object[][] userThenAdminPlayers() {
        return new Object[][] {
                {
                    Player.builder()
                            .login(generateRandomString("john_doe"))
                            .screenName(generateRandomString("John Doe"))
                            .password("vbervy3747bvvevn")
                            .age(32)
                            .gender("male")
                            .role("user")
                            .build()
                },
                {
                        Player.builder()
                                .login(generateRandomString("olivia_willson"))
                                .screenName(generateRandomString("Olivia_W"))
                                .password("bu3r4buf34")
                                .age(29)
                                .gender("female")
                                .role("admin")
                                .build()
                }
        };
    }

    @DataProvider
    public static Object[][] oneAdmin() {
        return new Object[][] {
                {
                        Player.builder()
                                .login(generateRandomString("super_admin"))
                                .password("BBUd6426fg28")
                                .screenName(generateRandomString("SuperAdmin"))
                                .role("admin")
                                .gender("male")
                                .age(40)
                                .build()
                }
        };
    }

    @DataProvider
    public static Object[][] customSupervisor() {
        return new Object[][] {
                {
                        Player.builder()
                                .screenName(generateRandomString("Supervisor_sister"))
                                .age(34)
                                .gender("female")
                                .login(generateRandomString("anotherSupervisor"))
                                .password("QwertY987")
                                .role("supervisor")
                                .build()
                }
        };
    }

    @DataProvider
    public static Object[][] userAndSupervisorThenUserAndAdmin() {
        return new Object[][] {
                {
                    Player.builder()
                            .login(generateRandomString("chrome_user"))
                            .password("vtde2fd")
                            .screenName(generateRandomString("Lucky_Player"))
                            .age(30)
                            .gender("female")
                            .role("user")
                            .build(),
                        Player.builder()
                                .screenName(generateRandomString("SuperSTAR"))
                                .age(30)
                                .gender("female")
                                .login(generateRandomString("StarSupervisor"))
                                .password("Byde2gd74")
                                .role("supervisor")
                                .build()
                },
                {
                        Player.builder()
                                .login(generateRandomString("unknown_user"))
                                .password("VEvnji5u")
                                .screenName(generateRandomString("UNKNOWN"))
                                .age(35)
                                .gender("male")
                                .role("user")
                                .build(),
                        Player.builder()
                                .screenName(generateRandomString("molly_cooper"))
                                .age(26)
                                .gender("female")
                                .login(generateRandomString("SweetMolly"))
                                .password("vn3igu5")
                                .role("admin")
                                .build()
                }
        };
    }

    @DataProvider
    public static Object[][] userAndAdmin() {
        return new Object[][] {
                {
                        Player.builder()
                                .login(generateRandomString("eric_user"))
                                .password("VEvnji5u")
                                .screenName(generateRandomString("EricAnders"))
                                .age(32)
                                .gender("male")
                                .role("user")
                                .build(),
                        Player.builder()
                                .screenName(generateRandomString("JillEpic"))
                                .age(28)
                                .gender("female")
                                .login(generateRandomString("jillian_epic_girl"))
                                .password("vn3igu5")
                                .role("admin")
                                .build()
                }
        };
    }

    private static String generateRandomString(final String substring) {
        return String.format("%s_%s", substring, UUID.randomUUID());
    }
}
