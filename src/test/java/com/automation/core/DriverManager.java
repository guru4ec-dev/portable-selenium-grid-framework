package com.automation.core;

import java.net.URI;
import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.http.ClientConfig;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

public class DriverManager {

        private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

        public static void initDriver(String browser){

        try {
            String execution = System.getProperty("execution", "remote");

            if (execution.equalsIgnoreCase("remote")) {

                String gridUrl = System.getenv("GRID_URL") != null
                        ? System.getenv("GRID_URL")
                        : System.getProperty("gridUrl", "http://localhost:4444");

                ClientConfig clientConfig = ClientConfig.defaultConfig()
                        .baseUrl(URI.create(gridUrl).toURL())
                        .connectionTimeout(Duration.ofSeconds(60))
                        .readTimeout(Duration.ofSeconds(120));

                HttpCommandExecutor executor = new HttpCommandExecutor(clientConfig);

                if (browser.equalsIgnoreCase("chrome")) {
                    ChromeOptions options = new ChromeOptions();
                    options.setPlatformName("LINUX");
                    driver.set(new RemoteWebDriver(executor, options));

                } else if (browser.equalsIgnoreCase("firefox")) {
                    FirefoxOptions options = new FirefoxOptions();
                    options.setPlatformName("LINUX");
                    driver.set(new RemoteWebDriver(executor, options));
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