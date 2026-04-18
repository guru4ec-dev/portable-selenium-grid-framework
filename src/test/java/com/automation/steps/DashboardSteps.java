package com.automation.steps;

import io.cucumber.java.en.*;

import com.automation.core.DriverManager;
import com.automation.pages.DashboardPage;
import com.automation.utils.ExcelUtils;

import org.testng.Assert;

import java.util.Map;

public class DashboardSteps {

    private static final String DATA_FILE  = "testdata/testdata.xlsx";
    private static final String SHEET_NAME = "Dashboard";  // must match sheet name in testdata.xlsx

    @Given("user is on dashboard page")
    public void openDashboard() {
        new DashboardPage(DriverManager.getDriver()).open();
    }

    @When("user performs dashboard action with test case {string}")
    public void performDashboardAction(String testCaseId) {
        Map<String, String> data = ExcelUtils.getTestDataById(DATA_FILE, SHEET_NAME, testCaseId);
        // TODO: call page methods with data values
        // new DashboardPage(DriverManager.getDriver()).someAction(data.get("ColumnName"));
    }

    @Then("dashboard result should be {string}")
    public void verifyDashboardResult(String expectedResult) {
        // TODO: add assertion
        // String actual = new DashboardPage(DriverManager.getDriver()).getResult();
        // Assert.assertEquals(actual, expectedResult);
    }
}
