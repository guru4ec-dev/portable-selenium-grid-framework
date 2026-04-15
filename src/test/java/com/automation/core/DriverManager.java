package com.automation.core;

import java.net.URI;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

public class DriverManager {

        private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

        public static void initDriver(String browser){

        try {
            String execution = System.getProperty("execution", "remote");

            if (execution.equalsIgnoreCase("remote")) {

                if (browser.equalsIgnoreCase("chrome")) {
                    ChromeOptions options = new ChromeOptions();
                    options.setPlatformName("LINUX");

                    driver.set(new RemoteWebDriver(
                            URI.create("http://localhost:4444").toURL(),
                            options
                    ));

                } else if (browser.equalsIgnoreCase("firefox")) {
                    FirefoxOptions options = new FirefoxOptions();
                    options.setPlatformName("LINUX");

                    driver.set(new RemoteWebDriver(
                            URI.create("http://localhost:4444").toURL(),
                            options
                    ));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
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