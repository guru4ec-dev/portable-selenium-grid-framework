package com.automation.hooks;

import io.cucumber.java.Before;
import io.cucumber.java.After;
import com.automation.core.DriverManager;


public class Hooks {

        @Before
        public void setUp() {
                System.out.println("Initializing Driver...");
                DriverManager.initDriver();
        }

        @After
        public void tearDown() {
            DriverManager.quitDriver();
        }
}
