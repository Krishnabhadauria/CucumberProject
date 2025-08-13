package com.CallTaxiService.cucumber.utils;

import java.io.*;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {

    public static List<Map<String, String>> getData(String sheetName) {
        List<Map<String, String>> dataList = new ArrayList<>();

        try (InputStream stream = ExcelUtil.class.getClassLoader().getResourceAsStream("BookingTestData.xlsx")) {
            if (stream == null) {
                throw new FileNotFoundException("Excel file not found in resources folder.");
            }

            Workbook workbook = new XSSFWorkbook(stream);
            Sheet sheet = workbook.getSheet(sheetName);

            Row headerRow = sheet.getRow(0);
            int rowCount = sheet.getPhysicalNumberOfRows();
            int colCount = headerRow.getPhysicalNumberOfCells();

            for (int i = 1; i < rowCount; i++) {
                Row currentRow = sheet.getRow(i);
                Map<String, String> rowData = new HashMap<>();

                for (int j = 0; j < colCount; j++) {
                    String key = headerRow.getCell(j).getStringCellValue();
                    String value = getCellValueAsString(currentRow.getCell(j));
                    rowData.put(key, value);
                }
                dataList.add(rowData);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataList;
    }

    // Utility method to handle all cell types
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            
            case BLANK:
                return "";
            default:
                return "";
        }
    }
}
