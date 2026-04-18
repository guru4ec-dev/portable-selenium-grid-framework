package com.automation.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

public class ExcelUtils {

    /**
     * Reads all rows from a specific sheet in an Excel file.
     * The first row is treated as the header.
     *
     * @param fileName  path relative to classpath (e.g. "testdata/testdata.xlsx")
     * @param sheetName sheet name matching the feature (e.g. "Login", "Dashboard")
     */
    public static List<Map<String, String>> getTestData(String fileName, String sheetName) {
        List<Map<String, String>> data = new ArrayList<>();

        try (InputStream is = ExcelUtils.class.getClassLoader().getResourceAsStream(fileName);
             Workbook workbook = new XSSFWorkbook(is)) {

            if (is == null) throw new RuntimeException("Test data file not found: " + fileName);

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) throw new RuntimeException("Sheet not found: " + sheetName);

            Row headerRow = sheet.getRow(0);
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue().trim());
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Map<String, String> rowData = new LinkedHashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    rowData.put(headers.get(j), getCellValue(cell));
                }
                data.add(rowData);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to read Excel file: " + fileName + " sheet: " + sheetName, e);
        }

        return data;
    }

    /**
     * Finds a single row by TestCaseId in the given sheet.
     *
     * @param fileName   path relative to classpath (e.g. "testdata/testdata.xlsx")
     * @param sheetName  sheet name matching the feature (e.g. "Login")
     * @param testCaseId value in the TestCaseId column
     */
    public static Map<String, String> getTestDataById(String fileName, String sheetName, String testCaseId) {
        return getTestData(fileName, sheetName).stream()
                .filter(row -> testCaseId.equals(row.get("TestCaseId")))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "TestCaseId '" + testCaseId + "' not found in sheet '" + sheetName + "'"));
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:  return cell.getStringCellValue().trim();
            case NUMERIC: return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default:      return "";
        }
    }
}

