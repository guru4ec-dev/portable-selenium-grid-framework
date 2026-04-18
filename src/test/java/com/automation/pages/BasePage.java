package com.automation.pages;

import org.openqa.selenium.WebDriver;

import com.automation.utils.ObjectRepository;

public class BasePage {

    protected WebDriver driver;

    public BasePage(WebDriver driver) {
        this.driver = driver;
    }

    public void type(String key, String value) {
        driver.findElement(ObjectRepository.get(key)).sendKeys(value);
    }

    public void click(String key) {
        driver.findElement(ObjectRepository.get(key)).click();
    }

    public String getText(String key) {
        return driver.findElement(ObjectRepository.get(key)).getText();
    }

    public void open(String urlKey) {
        driver.get(ObjectRepository.getString(urlKey));
    }

    public boolean isDisplayed(String key) {
        try {
            return driver.findElement(ObjectRepository.get(key)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clear(String key) {
        driver.findElement(ObjectRepository.get(key)).clear();
    }
}
