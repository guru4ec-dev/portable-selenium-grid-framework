package com.automation.steps;

import io.cucumber.java.en.*;

import com.automation.core.DriverManager;
import com.automation.pages.LoginPage;
import com.automation.utils.ExcelUtils;

import org.testng.Assert;

import java.util.Map;

public class LoginSteps {

    private static final String DATA_FILE  = "testdata/testdata.xlsx";
    private static final String SHEET_NAME = "Login";

    @Given("user is on login page")
    public void openLoginPage() {
        new LoginPage(DriverManager.getDriver()).open();
    }

    @When("user logs in with test case {string}")
    public void loginWithTestCase(String testCaseId) {
        Map<String, String> data = ExcelUtils.getTestDataById(DATA_FILE, SHEET_NAME, testCaseId);
        System.out.println("[DataDriven] TestCase: " + testCaseId +
                           " | Username: " + data.get("Username"));
        new LoginPage(DriverManager.getDriver()).login(data.get("Username"), data.get("Password"));
    }

    @Then("login result should be {string}")
    public void verifyLoginResult(String expectedResult) {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        if (expectedResult.equals("success")) {
            Assert.assertTrue(loginPage.getCurrentUrl().contains("/secure"),
                    "Expected /secure URL but got: " + loginPage.getCurrentUrl());
        } else {
            String flash = loginPage.getFlashMessage();
            Assert.assertTrue(flash.contains("Your username is invalid") || flash.contains("Your password is invalid"),
                    "Expected error message but got: " + flash);
        }
    }
}