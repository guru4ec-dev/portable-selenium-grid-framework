package com.automation.utils;

import java.io.*;
import java.util.*;

public class ExcelUtils {

    public static List<Map<String, String>> getTestData(String fileName) {
        List<Map<String, String>> data = new ArrayList<>();

        try (InputStream is = ExcelUtils.class.getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) throw new RuntimeException("Test data file not found: " + fileName);

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String headerLine = reader.readLine();
            if (headerLine == null) return data;

            String[] headers = headerLine.split(",");
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] values = line.split(",", -1);
                Map<String, String> row = new LinkedHashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    row.put(headers[i].trim(), i < values.length ? values[i].trim() : "");
                }
                data.add(row);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read test data file: " + fileName, e);
        }

        return data;
    }

    public static Map<String, String> getTestDataById(String fileName, String testCaseId) {
        return getTestData(fileName).stream()
                .filter(row -> testCaseId.equals(row.get("TestCaseId")))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Test case not found: " + testCaseId));
    }
}
