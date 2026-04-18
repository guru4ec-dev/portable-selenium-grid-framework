package com.automation.stepdefinitions;

import io.cucumber.java.en.*;

import com.automation.core.DriverManager;
import com.automation.pages.LoginPage;

import org.testng.Assert;

public class LoginSteps {

    @Given("user is on login page")
    public void openLoginPage() {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        loginPage.open();
    }

    @When("user enters {string} and {string}")
    public void enterCredentials(String username, String password) {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        loginPage.login(username, password);
    }

    @Then("user should land on dashboard")
    public void verifyDashboard() {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        String currentUrl = loginPage.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/wrong"),
                "Expected dashboard URL to contain '/wrong' but got: " + currentUrl);
        System.out.println("Login successful - URL: " + currentUrl);
    }
}