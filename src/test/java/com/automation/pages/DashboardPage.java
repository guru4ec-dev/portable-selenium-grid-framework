package com.automation.pages;

import org.openqa.selenium.WebDriver;

public class DashboardPage extends BasePage {

    public DashboardPage(WebDriver driver) { super(driver); }

    public void open() { open("dashboard.url"); }

    // TODO: add page action methods using BasePage keywords:
    // type("dashboard.fieldName", value)
    // click("dashboard.buttonName")
    // getText("dashboard.elementName")
    // isDisplayed("dashboard.elementName")
}
