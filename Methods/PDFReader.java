package Methods;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PDFReader {

	public static void main(String[] args) throws IOException {
		
		
		String pdfPath =ConfiReader.get("pdfPath");  
		
		PDDocument document= PDDocument.load(new File(pdfPath));
		
		PDFTextStripper stepper=new PDFTextStripper();
		String text=stepper.getText(document);
		document.close();
		
		System.out.println(text);
		
		FileWriter writer = new FileWriter(ConfiReader.get("PDFOutputText"));
        writer.write(text);
        writer.close();

        System.out.println("✅ PDF content also saved to PDFOutput.txt");
		

	}

}
