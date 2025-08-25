package Methods;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



public class ReadDataFromExcel {
	
	public static List<Map<String, String>> ReadData() throws IOException {
        String filePath = ConfiReader.get("filePath");
        FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(fis);

        Sheet sheet = workbook.getSheet(ConfiReader.get("SheetName"));

        List<Map<String, String>> excelData = new ArrayList<>();
        Row headerRow = sheet.getRow(0);
        int colCount = headerRow.getLastCellNum();
        int rowCount = sheet.getPhysicalNumberOfRows();

        for (int i = 1; i < rowCount; i++) {
        	
            Row row = sheet.getRow(i);
            if (row == null || isRowEmpty(row)) continue;

            Map<String, String> rowData = new LinkedHashMap<>();
            for (int j = 0; j < colCount; j++) {
                String key = getCellValueAsString(headerRow.getCell(j));
                String value = getCellValueAsString(row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
                rowData.put(key, value);
            }
            excelData.add(rowData);
        }

        workbook.close();
        fis.close();
        return excelData;
    }
    private static boolean isRowEmpty(Row row) {
	    if (row == null) return true;
	    for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
	        Cell cell = row.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
	        if (cell != null && cell.getCellType() != CellType.BLANK) {
	            return false;
	        }
	    }
	    return true;
	}

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((int) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case BLANK:
                return "";
            default:
                return cell.toString();
        }
    }

}
