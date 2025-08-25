package Methods;

import java.io.FileInputStream;
import java.util.Properties;

public class ConfiReader {
	
	private static Properties properties;
	 
    public static String get(String key) {
        if (properties == null) {
            try {
                properties = new Properties();
                FileInputStream fis = new FileInputStream("C:\\Users\\murari.n\\eclipse-workspace\\Custom_Method\\src\\test\\java\\Methods\\TestData.properties");
                properties.load(fis);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return properties.getProperty(key);
        
    }

}
