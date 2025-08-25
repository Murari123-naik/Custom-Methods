package Methods;

import java.io.File;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JSONConverter {
	

	public static void main(String[] args) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT); 
            List<Map<String, String>> excelData = ReadDataFromExcel.ReadData();          
            File outputFile = new File(ConfiReader.get("outPutFilepath"));
            mapper.writeValue(outputFile, excelData);         
            System.out.println("✅ JSON written to: " + outputFile.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
