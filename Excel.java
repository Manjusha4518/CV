package base;

import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

public class Excel {
    private static Map<String, CellStyle> cellStyles = new HashMap<>();

    public static void main(String[] args) {
        ZipSecureFile.setMinInflateRatio(0.00001);
        ZipSecureFile.setMaxEntrySize(Long.MAX_VALUE);
        ZipSecureFile.setMaxTextSize(Long.MAX_VALUE);

        String filePath = "C:\\Projects\\ShinkaP2_QA_Automation\\EIS\\testData\\TestData_QA.xlsx";
        List<String> value = Arrays.asList("Yes");

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            // Initialize reusable styles once
            initializeStyles(workbook);

            // Part 5: Find Row and Update Cell based on the other cell value and column name
            List<String> prefixes = Arrays.asList("");

            for(int i = 0; i <= prefixes.size() - 1; i++) {
                findRowAndUpdateCell(workbook, prefixes.get(i), "", "",
                        "", "");

            }

            // Save the workbook back to the original file
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
                System.out.println("Workbook updated and saved to: " + filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void initializeStyles(Workbook workbook) {
        // Bordered style
        CellStyle borderedStyle = workbook.createCellStyle();
        borderedStyle.setBorderTop(BorderStyle.THIN);
        borderedStyle.setBorderBottom(BorderStyle.THIN);
        borderedStyle.setBorderLeft(BorderStyle.THIN);
        borderedStyle.setBorderRight(BorderStyle.THIN);
        cellStyles.put("borderedStyle", borderedStyle);

        // Orange header style with borders
        CellStyle orangeHeaderStyle = workbook.createCellStyle();
        orangeHeaderStyle.cloneStyleFrom(borderedStyle);
        orangeHeaderStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
        orangeHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = workbook.createFont();
        font.setBold(true);
        orangeHeaderStyle.setFont(font);
        cellStyles.put("orangeHeaderStyle", orangeHeaderStyle);
    }

    public static void applyOrangeFillToHeader(Cell targetCell) {
        CellStyle style = cellStyles.get("orangeHeaderStyle");
        if (style != null) {
            targetCell.setCellStyle(style);
        } else {
            System.err.println("Style 'orangeHeaderStyle' not found.");
        }
    }

    public static void applyBordersToCell(Cell cell) {
        CellStyle style = cellStyles.get("borderedStyle");
        if (style != null) {
            cell.setCellStyle(style);
        } else {
            System.err.println("Style 'borderedStyle' not found.");
        }
    }

    public static void addColumnsToSheet(Workbook workbook, String sheetName, List<String> columnNames) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            System.out.println("Sheet " + sheetName + " does not exist.");
            return;
        }

        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            headerRow = sheet.createRow(0);
        }

        for (String columnName : columnNames) {
            int columnIndex = headerRow.getLastCellNum() == -1 ? 0 : headerRow.getLastCellNum();
            Cell newCell = headerRow.createCell(columnIndex);
            newCell.setCellValue(columnName);
            applyOrangeFillToHeader(newCell); // Use reusable style
            System.out.println("Added column: " + columnName + " to sheet: " + sheetName);
        }
    }

    public static void addRowsToSheetWithPrefix(Workbook workbook, String sheetPrefix, int numRows) {
        boolean sheetFound = false;

        // Iterate through all sheets to find the ones with the specified prefix
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (sheet.getSheetName().startsWith(sheetPrefix)) {
                sheetFound = true;
                int lastRowNum = sheet.getLastRowNum();
                Row headerRow = sheet.getRow(0);
                int lastColumn = headerRow != null ? headerRow.getLastCellNum() : 0;
                for (int j = 1; j <= numRows; j++) {
                    int rowIndex = lastRowNum + j;
                    Row newRow = sheet.createRow(rowIndex);
                    // Create cells in the new row and apply borders
                    for (int k = 0; k < lastColumn; k++) {
                        Cell newCell = newRow.createCell(k);
                        applyBordersToCell(newCell); // Use reusable bordered style
                    }
                }
                System.out.println("Added " + numRows + " rows to sheet: " + sheet.getSheetName() + " with borders applied.");
            }
        }
        if (!sheetFound) {
            System.out.println("No sheets with the prefix '" + sheetPrefix + "' were found.");
        }
    }

    public static void addValueToCellsInColumn(Workbook workbook, String sheetPrefix, String columnName, Object value, Integer rowIndex) {
        boolean sheetFound = false;
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (sheet.getSheetName().startsWith(sheetPrefix)) {
                sheetFound = true;
                Row headerRow = sheet.getRow(0);
                if (headerRow == null) {
                    System.out.println("No header row found in sheet " + sheet.getSheetName());
                    continue;
                }
                int columnIndex = -1;
                for (int j = 0; j < headerRow.getLastCellNum(); j++) {
                    Cell cell = headerRow.getCell(j);
                    if (cell != null && cell.getStringCellValue().equals(columnName)) {
                        columnIndex = j;
                        break;
                    }
                }
                if (columnIndex == -1) {
                    System.out.println("Column " + columnName + " not found in sheet " + sheet.getSheetName());
                    continue;
                }

                // If the value is a list, handle it differently
                if (value instanceof List) {
                    List<String> values = (List<String>) value;
                    int startRowIndex = Math.max(1, rowIndex);
                    for (int j = 0; j < values.size(); j++) {
                        Row row = sheet.getRow(startRowIndex + j);
                        if (row == null) row = sheet.createRow(startRowIndex + j);
                        Cell cell = row.createCell(columnIndex);
                        cell.setCellValue(values.get(j));
                        applyBordersToCell(cell); // Use reusable bordered style
                    }
                } else {
                    // Handle a single value
                    Row row = sheet.getRow(rowIndex);
                    if (row == null) row = sheet.createRow(rowIndex);
                    Cell cell = row.createCell(columnIndex);
                    cell.setCellValue((String) value);
                    applyBordersToCell(cell); // Use reusable bordered style
                }
                System.out.println("Added values to column: " + columnName + " in sheet: " + sheet.getSheetName());
            }
        }
        if (!sheetFound) {
            System.out.println("No sheets with the prefix '" + sheetPrefix + "' were found.");
        }
    }

    public static void findRowAndUpdateCell(Workbook workbook, String sheetPrefix, String emptyCellColumnName, String fullCellColumnName,
                                            String fullColumnNameValue, Object valueForEmptyCell) {
        boolean sheetFound = false;
        boolean productFound = false;

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (sheet.getSheetName().startsWith(sheetPrefix)) {
                sheetFound = true;
                Row headerRow = sheet.getRow(0);
                if (headerRow == null) {
                    System.out.println("No header row found in sheet " + sheet.getSheetName());
                    continue;
                }
                int fullCellColumnIndex = -1;
                int emptyCellColumnIndex = -1;
                int lastRowNum = sheet.getLastRowNum();

                // Find column indices for full and empty cell column names
                for (int j = 0; j < headerRow.getLastCellNum(); j++) {
                    Cell cell = headerRow.getCell(j);
                    if (cell != null) {
                        String headerValue = cell.getStringCellValue().trim();
                        if (headerValue.equalsIgnoreCase(fullCellColumnName.trim())) {
                            fullCellColumnIndex = j;
                        }
                        if (headerValue.equalsIgnoreCase(emptyCellColumnName.trim())) {
                            emptyCellColumnIndex = j;
                        }
                    }
                }

                if (fullCellColumnIndex == -1 || emptyCellColumnIndex == -1) {
                    System.out.println("One of the specified columns not found in sheet " + sheet.getSheetName());
                    continue;
                }

                // Find rows matching the fullColumnNameValue and update emptyCellColumnName in that row
                for (int k = 1; k <= lastRowNum; k++) {
                    Row row = sheet.getRow(k);
                    if (row != null) {
                        Cell fullCell = row.getCell(fullCellColumnIndex);
                        if (fullCell != null && fullColumnNameValue.equalsIgnoreCase(fullCell.getStringCellValue().trim())) {
                            productFound = true;
                            Cell emptyCell = row.getCell(emptyCellColumnIndex);
                            if (emptyCell == null) emptyCell = row.createCell(emptyCellColumnIndex);
                            emptyCell.setCellValue((String) valueForEmptyCell);
                            applyBordersToCell(emptyCell); // Apply bordered style
                            System.out.println("Updated cell in row " + (k + 1) + " in column: " + emptyCellColumnName);
                        }
                    }
                }

                if (!productFound) {
                    System.out.println("No rows with value '" + fullColumnNameValue + "' found in column " + fullCellColumnName);
                }
            }
        }
        if (!sheetFound) {
            System.out.println("No sheets with prefix '" + sheetPrefix + "' were found.");
        }
    }

}