package com.automation.hooks;

import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Scenario;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import com.automation.core.DriverManager;

public class Hooks {

    // Screenshot modes:
    //   failure    (default) — only on scenario failure
    //   step-all             — after every step (pass or fail)
    //   step-failed          — after only failed steps
    // Set via: -Dscreenshot.mode=step-all  or  -Dscreenshot.mode=step-failed
    private static final String SCREENSHOT_MODE = System.getProperty("screenshot.mode", "failure");

    @Before
    public void setUp(Scenario scenario) {
        String browser = System.getProperty("browser", "chrome");
        System.out.println("Thread: " + Thread.currentThread().getName() +
                           " Browser: " + browser);
        DriverManager.initDriver(browser);
    }

    @AfterStep
    public void afterStep(Scenario scenario) {
        if (DriverManager.getDriver() == null) return;

        boolean captureAll    = "step-all".equalsIgnoreCase(SCREENSHOT_MODE);
        boolean captureFailed = "step-failed".equalsIgnoreCase(SCREENSHOT_MODE) && scenario.isFailed();

        if (captureAll || captureFailed) {
            byte[] screenshot = ((TakesScreenshot) DriverManager.getDriver())
                    .getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "step-screenshot");
        }
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