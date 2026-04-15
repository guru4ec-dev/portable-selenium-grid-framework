package com.automation.hooks;

import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;

import java.util.concurrent.atomic.AtomicInteger;

import com.automation.core.DriverManager;

public class Hooks {

    private static AtomicInteger counter = new AtomicInteger(0);
    private static final String[] BROWSERS = {"chrome", "firefox"};

    @Before
    public void setUp(Scenario scenario) {

        String browser = System.getProperty("browser");

        if (browser == null) {
            int index = counter.getAndIncrement() % BROWSERS.length;
            browser = BROWSERS[index];
        }

        System.out.println("Thread: " + Thread.currentThread().getName() +
                           " Browser: " + browser);

        DriverManager.initDriver(browser);
    }

    @After
    public void tearDown() {
        DriverManager.quitDriver();
    }
}