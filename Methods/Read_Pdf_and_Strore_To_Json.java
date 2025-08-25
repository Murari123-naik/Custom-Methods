package Methods;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Read_Pdf_and_Strore_To_Json {
	
	
	public static void main(String[] args) {
		
		 String pdfPath = ConfiReader.get("pdfPath");   
	        String jsonPath = ConfiReader.get("jsonPath"); 
	        try {
	            
	            PDDocument document = PDDocument.load(new File(pdfPath));

	            
	            PDFTextStripper stripper = new PDFTextStripper();
	            String text = stripper.getText(document);
	            document.close();

	            
	            Map<String, String> dataMap = new LinkedHashMap<>();
	            String[] lines = text.split("\n");

	            for (String line : lines) {
	                if (line.contains(":")) {  
	                    String[] parts = line.split(":", 2);
	                    dataMap.put(parts[0].trim(), parts[1].trim());
	                }
	            }

	           
	            ObjectMapper mapper = new ObjectMapper();
	            mapper.writerWithDefaultPrettyPrinter().writeValue(new FileWriter(jsonPath), dataMap);

	            System.out.println("✅ PDF content saved to " + jsonPath);

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

}
