package com.automation.pages;

import org.openqa.selenium.WebDriver;


public class LoginPage extends BasePage {

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        open("login.url");
    }

    public void login(String username, String password) {
        type("login.usernameField", username);
        type("login.passwordField", password);
        click("login.loginButton");
    }

    public String getFlashMessage() {
        try {
            return getText("login.flashMessage");
        } catch (Exception e) {
            return "";
        }
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}


