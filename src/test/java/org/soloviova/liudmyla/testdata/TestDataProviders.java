package org.soloviova.liudmyla.testdata;

import org.testng.annotations.DataProvider;

public class TestDataProviders {

    @DataProvider
    private static Object[][] wrongUrisForGetAllPlayers() {
        return new Object[][] {
                {"/get/all-players"},
                {"/get"}
        };
    }
}
