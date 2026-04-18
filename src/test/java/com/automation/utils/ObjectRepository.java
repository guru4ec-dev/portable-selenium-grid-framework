package com.automation.utils;

import org.openqa.selenium.By;

import java.io.InputStream;
import java.util.Properties;

public class ObjectRepository {

    private static final Properties props = new Properties();

    static {
        try (InputStream in = ObjectRepository.class.getClassLoader()
                .getResourceAsStream("locators.properties")) {
            if (in == null) throw new RuntimeException("locators.properties not found on classpath");
            props.load(in);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load locators.properties", e);
        }
    }

    /**
     * Returns a Selenium By locator for the given key.
     * Key format: pageName.elementName  (e.g. "login.usernameField")
     * Value format in properties: locatorType:locatorValue  (e.g. "id:username")
     */
    public static By get(String key) {
        String value = props.getProperty(key);
        if (value == null) throw new RuntimeException("Locator not found for key: " + key);

        String[] parts = value.split(":", 2);
        String type = parts[0].trim().toLowerCase();
        String locator = parts[1].trim();

        switch (type) {
            case "id":       return By.id(locator);
            case "css":      return By.cssSelector(locator);
            case "xpath":    return By.xpath(locator);
            case "name":     return By.name(locator);
            case "linktext": return By.linkText(locator);
            default: throw new RuntimeException("Unknown locator type '" + type + "' for key: " + key);
        }
    }

    /**
     * Returns a plain string value for the given key (e.g. URLs).
     */
    public static String getString(String key) {
        String value = props.getProperty(key);
        if (value == null) throw new RuntimeException("Property not found for key: " + key);
        return value.trim();
    }
}
