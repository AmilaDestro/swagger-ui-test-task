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
    public static Object[][] playersWithEditorRoleForCreation() {
        return new Object[][] {
                {
                    Player.builder()
                            .login("Adventurer")
                            .password("VyqvTtY736JJSD")
                            .screenName("Alice Ashcroft")
                            .gender("female")
                            .age(24)
                            .role("user")
                            .build(),
                        "admin"
                },
                {
                        Player.builder()
                                .login("thomas_birne")
                                .password("uTCcvjew64ejd3")
                                .screenName("Tom Birne")
                                .gender("male")
                                .age(29)
                                .role("admin")
                                .build(),
                        "supervisor"
                },
                {
                        Player.builder()
                                .login("Starkiller99")
                                .password("nnjcei2gc")
                                .screenName("Neil_Starkiller")
                                .gender("male")
                                .age(23)
                                .role("user")
                                .build(),
                        "supervisor"
                },
                {
                        Player.builder()
                                .login("hanna_epic")
                                .password("Bhcuweyyvxcuq")
                                .screenName("Johanna_The_One")
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
                },
                {
                        Player.builder()
                                .age(27)
                                .role("user")
                                .gender("male")
                                .login("alex_smith")
                                .password("qwert09Password0123")
                                .screenName("AleXSmith")
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

    @DataProvider
    public static Object[][] oneUser() {
        return new Object[][] {
                {
                        Player.builder()
                                .login("richard_bloom")
                                .screenName("Richard_Bloom_Jr")
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
                            .login("john_doe")
                            .screenName("John Doe")
                            .password("vbervy3747bvvevn")
                            .age(32)
                            .gender("male")
                            .role("user")
                            .build()
                },
                {
                        Player.builder()
                                .login("olivia_willson")
                                .screenName("Olivia_W")
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
                                .login("super_admin")
                                .password("BBUd6426fg28")
                                .screenName("SuperAdmin")
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
                                .screenName("Supervisor_sister")
                                .age(34)
                                .gender("female")
                                .login("anotherSupervisor")
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
                            .login("chrome_user")
                            .password("vtde2fd")
                            .screenName("Lucky_Player")
                            .age(30)
                            .gender("female")
                            .role("user")
                            .build(),
                        Player.builder()
                                .screenName("SuperSTAR")
                                .age(30)
                                .gender("female")
                                .login("StarSupervisor")
                                .password("Byde2gd74")
                                .role("supervisor")
                                .build()
                },
                {
                        Player.builder()
                                .login("unknown_user")
                                .password("VEvnji5u")
                                .screenName("UNKNOWN")
                                .age(35)
                                .gender("male")
                                .role("user")
                                .build(),
                        Player.builder()
                                .screenName("molly_cooper")
                                .age(26)
                                .gender("female")
                                .login("SweetMolly")
                                .password("vn3igu5")
                                .role("admin")
                                .build()
                }
        };
    }
}
