package com.automation.core;

import java.net.URL;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DriverManager {

    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static void initDriver() {
        try {
            String execution = System.getProperty("execution", "local");

            if (execution.equalsIgnoreCase("remote")) {

                ChromeOptions options = new ChromeOptions();
                options.addArguments("--start-maximized");

                driver.set(new RemoteWebDriver(
                        new URL("http://localhost:4444/wd/hub"),
                        options
                ));

            } else {
                driver.set(new ChromeDriver());
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize WebDriver", e);
        }
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}