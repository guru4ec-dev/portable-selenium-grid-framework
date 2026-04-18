package com.automation.stepdefinitions;

import io.cucumber.java.en.*;

import com.automation.core.DriverManager;
import com.automation.pages.LoginPage;
import com.automation.utils.ExcelUtils;

import org.testng.Assert;

import java.util.Map;

public class LoginSteps {

    private static final String DATA_FILE = "testdata/login_data.xlsx";
    private static final String SHEET     = "LoginTests";

    @Given("user is on login page")
    public void openLoginPage() {
        new LoginPage(DriverManager.getDriver()).open();
    }

    @When("user logs in with test case {string}")
    public void loginWithTestCase(String testCaseId) {
        Map<String, String> data = ExcelUtils.getTestDataById(DATA_FILE, SHEET, testCaseId);
        String username = data.get("Username");
        String password = data.get("Password");
        System.out.println("[DataDriven] TestCase: " + testCaseId +
                           " | Username: " + username + " | Password: " + password);
        new LoginPage(DriverManager.getDriver()).login(username, password);
    }

    @Then("login result should be {string}")
    public void verifyLoginResult(String expectedResult) {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        if (expectedResult.equals("success")) {
            Assert.assertTrue(loginPage.getCurrentUrl().contains("/secure"),
                    "Expected /secure URL but got: " + loginPage.getCurrentUrl());
            System.out.println("Login successful - URL: " + loginPage.getCurrentUrl());
        } else {
            String flash = loginPage.getFlashMessage();
            Assert.assertTrue(flash.contains("Your username is invalid") || flash.contains("Your password is invalid"),
                    "Expected error message but got: " + flash);
            System.out.println("Login failed as expected - Message: " + flash);
        }
    }
}