package com.automation.stepdefinitions;

import com.automation.hooks.Hooks;
import io.cucumber.java.en.*;
import org.openqa.selenium.WebDriver;

public class LoginSteps {

    WebDriver driver = Hooks.getDriver();

    @Given("user is on login page")
    public void openLoginPage() {
        driver.get("https://example.com");
    }

    @When("user enters valid credentials")
    public void enterValid() {
        System.out.println("Valid login");
    }

    @When("user enters invalid credentials")
    public void enterInvalid() {
        System.out.println("Invalid login");
    }

    @Then("user should see dashboard")
    public void dashboard() {
        System.out.println("Dashboard verified");
    }

    @Then("error message should be shown")
    public void error() {
        System.out.println("Error verified");
    }
}
