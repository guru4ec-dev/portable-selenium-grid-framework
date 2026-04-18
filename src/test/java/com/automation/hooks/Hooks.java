package com.automation.hooks;

import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import com.automation.core.DriverManager;

public class Hooks {

    @Before
    public void setUp(Scenario scenario) {
        String browser = System.getProperty("browser", "chrome");
        System.out.println("Thread: " + Thread.currentThread().getName() +
                           " Browser: " + browser);
        DriverManager.initDriver(browser);
    }

    @After
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed() && DriverManager.getDriver() != null) {
            byte[] screenshot = ((TakesScreenshot) DriverManager.getDriver())
                    .getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "failure-screenshot");
        }
        DriverManager.quitDriver();
    }
}