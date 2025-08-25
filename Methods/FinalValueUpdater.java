package Methods;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FinalValueUpdater {
	
	public static void main(String[] args) {
		
        String equalKeysPath = "C:\\Users\\murari.n\\Documents\\EqualKeys.xlsx";
        String finalValuePath = "C:\\Users\\murari.n\\Documents\\FinalValueSheet.xlsx";
        String pdfPath = "C:\\Users\\murari.n\\Downloads\\EmployeDataa.pdf.pdf";

        try {
            Map<String, String> keyToCol1 = buildKeyMapping(equalKeysPath);

            List<Map<String, String>> employeeDataList = readEmployeeDataFromPDF(pdfPath);

            Workbook finalWorkbook = new XSSFWorkbook();
            Sheet finalSheet = finalWorkbook.createSheet("Sheet1");

            
            List<String> col1Headers = new ArrayList<>(new LinkedHashSet<>(keyToCol1.values()));

            Row headerRow = finalSheet.createRow(0);
            for (int c = 0; c < col1Headers.size(); c++) {
                headerRow.createCell(c).setCellValue(col1Headers.get(c));
            }

            
            for (int i = 0; i < employeeDataList.size(); i++) {
                Map<String, String> emp = employeeDataList.get(i);
                Row row = finalSheet.createRow(i + 1);

                for (int col = 0; col < col1Headers.size(); col++) {
                    String header = col1Headers.get(col);
                    String value = null;

                    for (Map.Entry<String, String> entry : keyToCol1.entrySet()) {
                        String pdfKey = entry.getKey();
                        String mappedCol1 = entry.getValue();

                        if (mappedCol1.equals(header)) {
                            for (String actualPdfKey : emp.keySet()) {
                                String normalizedPdfKey = normalize(actualPdfKey);
                                if (normalizedPdfKey.equals(pdfKey)) {
                                    value = emp.get(actualPdfKey);
                                    break;
                                }
                            }
                        }
                        if (value != null) break;
                    }
                    row.createCell(col).setCellValue(value != null ? value : "");
                }
            }

            FileOutputStream fos = new FileOutputStream(finalValuePath);
            finalWorkbook.write(fos);
            fos.close();
            finalWorkbook.close();

            System.out.println("✅ FinalValueSheet.xlsx updated successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   
    private static Map<String, String> buildKeyMapping(String path) {
        Map<String, String> mapping = new LinkedHashMap<>();
        try (FileInputStream fis = new FileInputStream(path);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String col1 = row.getCell(0).getStringCellValue().trim();
                String col2 = row.getCell(1).getStringCellValue().trim();

                if (!col1.isEmpty() && !col2.isEmpty()) {
                    mapping.put(normalize(col1), col1);
                    mapping.put(normalize(col2), col1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapping;
    }

    
    private static List<Map<String, String>> readEmployeeDataFromPDF(String pdfPath) {
        List<Map<String, String>> employees = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            PDDocument document = PDDocument.load(new File(pdfPath));
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            document.close();

            
            Matcher matcher = Pattern.compile("\\{.*?\\}", Pattern.DOTALL).matcher(text);

            while (matcher.find()) {
                String jsonObject = matcher.group();
                jsonObject = cleanJson(jsonObject);  

                try {
                    JsonNode rootNode = mapper.readTree(jsonObject);
                    Map<String, String> empMap = new LinkedHashMap<>();
                    flattenJson(rootNode, "", empMap);
                    employees.add(empMap);
                } catch (Exception ex) {
                    System.err.println("⚠️ Skipping invalid JSON block: " + ex.getMessage());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return employees;
    }

    
    private static void flattenJson(JsonNode node, String prefix, Map<String, String> result) {
        if (node.isObject()) {
            node.fieldNames().forEachRemaining(field -> {
                flattenJson(node.get(field), prefix.isEmpty() ? field : prefix + "." + field, result);
            });
        } else if (node.isArray()) {
            List<String> values = new ArrayList<>();
            for (JsonNode child : node) {
                if (child.isValueNode()) {
                    values.add(child.asText());
                } else {
                    Map<String, String> nested = new LinkedHashMap<>();
                    flattenJson(child, "", nested);
                    values.add(nested.toString());
                }
            }
            result.put(prefix, String.join(", ", values));
        } else if (node.isValueNode()) {
            result.put(prefix, node.asText());
        }
    }

    
    private static String normalize(String s) {
        return s.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    
    private static String cleanJson(String text) {
        return text
                .replaceAll("[“”]", "\"")   
                .replaceAll("[‘’]", "'")   
                .replaceAll("\u00A0", " ") 
                .trim();
    }

}
