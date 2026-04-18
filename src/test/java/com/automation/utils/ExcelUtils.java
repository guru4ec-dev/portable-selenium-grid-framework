package com.automation.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtils {

    /**
     * Reads all rows from the given sheet in the Excel file located in src/test/resources/.
     * Returns a list of maps where key = column header, value = cell value.
     */
    public static List<Map<String, String>> getTestData(String fileName, String sheetName) {
        List<Map<String, String>> data = new ArrayList<>();

        try {
            InputStream is = ExcelUtils.class.getClassLoader().getResourceAsStream(fileName);
            if (is == null) {
                // fallback: read directly from src/test/resources (useful before Maven copies resources)
                is = new FileInputStream("src/test/resources/" + fileName);
            }

            try (Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheet(sheetName);
            Row headerRow = sheet.getRow(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Map<String, String> rowData = new LinkedHashMap<>();
                for (int j = 0; j < headerRow.getLastCellNum(); j++) {
                    String header = headerRow.getCell(j).getStringCellValue().trim();
                    Cell cell = row.getCell(j);
                    String value = (cell != null) ? cell.toString().trim() : "";
                    rowData.put(header, value);
                }
                data.add(rowData);
            }

            } // end try-with-resources workbook

        } catch (Exception e) {
            throw new RuntimeException("Failed to read Excel file: " + fileName, e);
        }

        return data;
    }

    /**
     * Finds a single row matching the given testCaseId from the TestCaseId column.
     */
    public static Map<String, String> getTestDataById(String fileName, String sheetName, String testCaseId) {
        return getTestData(fileName, sheetName).stream()
                .filter(row -> testCaseId.equals(row.get("TestCaseId")))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Test case not found: " + testCaseId));
    }

    /**
     * Creates the login test data Excel file at runtime if it doesn't exist in resources.
     * Call this once from a @BeforeSuite or setup method.
     */
    public static void createLoginTestDataIfNeeded() {
        // Write to src/test/resources/testdata so it's always available on classpath
        String basePath = "src/test/resources/testdata/login_data.xlsx";
        File file = new File(basePath);

        if (file.exists()) return;

        file.getParentFile().mkdirs();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("LoginTests");

            // Header row
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("TestCaseId");
            header.createCell(1).setCellValue("Username");
            header.createCell(2).setCellValue("Password");
            header.createCell(3).setCellValue("ExpectedResult");

            // Test data rows
            Object[][] testData = {
                {"ValidLogin",      "tomsmith", "SuperSecretPassword!", "success"},
                {"InvalidPassword", "tomsmith", "wrongpassword",        "failure"},
                {"InvalidUsername", "wronguser", "SuperSecretPassword!", "failure"}
            };

            for (int i = 0; i < testData.length; i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < testData[i].length; j++) {
                    row.createCell(j).setCellValue(testData[i][j].toString());
                }
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }

            System.out.println("[ExcelUtils] Created test data file: " + file.getAbsolutePath());

        } catch (Exception e) {
            throw new RuntimeException("Failed to create test data file", e);
        }
    }
}
