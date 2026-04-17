package com.automation.core;

import java.net.URI;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

public class DriverManager {

        private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

        public static void initDriver(String browser) {
            String gridUrl = System.getenv("GRID_URL") != null
                    ? System.getenv("GRID_URL")
                    : System.getProperty("gridUrl", "http://localhost:4444");

            int maxRetries = 5;
            int retryDelaySecs = 10;

            for (int attempt = 1; attempt <= maxRetries; attempt++) {
                try {
                    if (browser.equalsIgnoreCase("chrome")) {
                        ChromeOptions options = new ChromeOptions();
                        options.setPlatformName("LINUX");
                        driver.set(new RemoteWebDriver(URI.create(gridUrl).toURL(), options));

                    } else if (browser.equalsIgnoreCase("firefox")) {
                        FirefoxOptions options = new FirefoxOptions();
                        options.setPlatformName("LINUX");
                        driver.set(new RemoteWebDriver(URI.create(gridUrl).toURL(), options));
                    }
                    return; // success

                } catch (Exception e) {
                    System.out.println("[DriverManager] Session creation failed (attempt " + attempt + "/" + maxRetries + "): " + e.getMessage());
                    if (attempt == maxRetries) {
                        throw new RuntimeException("Failed to create session after " + maxRetries + " attempts", e);
                    }
                    try {
                        Thread.sleep(retryDelaySecs * 1000L);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
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