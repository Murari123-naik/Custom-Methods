package Methods;

import static io.restassured.RestAssured.given;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import org.testng.annotations.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class CreateParameter {
	
	 
	 private static String token;
	    private static String accessToken;
	    private static FileWriter writer;

	    public static void main(String[] args) {
	        try {
	            
	            writer = new FileWriter("C:\\Users\\murari.n\\Documents\\ApiOutput.txt", false);

	            getToken();
	            authToken();

	            int id = createParameter(); 
	            deleteParameter(id);

	            writer.close(); 
	            System.out.println("✅ All responses saved to ApiOutput.txt");

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    public static void logToFile(String message) {
	        try {
	            writer.write(message + "\n"); 
	            writer.flush(); 
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    public static void getToken() {
	        RestAssured.baseURI = ConfiReader.get("BaseURI");

	        String requestBody = "{\"email\": \"murari.n@simplify3x.com\", \"type\": \"Web\"}";

	        Response response = given()
	                .header("Content-Type", "application/json")
	                .body(requestBody)
	                .when()
	                .post(ConfiReader.get("gettokenendpoint"))
	                .then()
	                .statusCode(200)
	                .extract()
	                .response();

	        token = response.jsonPath().getString("token");
	        String logMsg = "✅ Token: " + token;
	        System.out.println(logMsg);
	        logToFile(logMsg);
	    }

	    public static void authToken() {
	        String requestBody = "{"
	                + "\"email\":\"murari.n@simplify3x.com\","
	                + "\"password\":\"MURari@@122\","
	                + "\"companyName\":\"UAT\","
	                + "\"type\":\"web\"}";

	        Response response = given()
	                .header("Content-Type", "application/json")
	                .header("Authorization", token)
	                .body(requestBody)
	                .when()
	                .post(ConfiReader.get("authtokenendpoint"))
	                .then()
	                .statusCode(200)
	                .extract()
	                .response();

	        accessToken = response.jsonPath().getString("accessToken");
	        String logMsg = "✅ Access Token: " + accessToken;
	        System.out.println(logMsg);
	        logToFile(logMsg);
	    }

	    public static int createParameter() {
	        String characters = "abcdefghijklmnopqrstuvwxyz";
	        String ParameterName = "";
	        Random rand = new Random();
	        for (int i = 0; i < 8; i++) {
	            ParameterName += characters.charAt(rand.nextInt(characters.length()));
	        }

	        String requestBody = "{"
	                + "\"name\":\"" + ParameterName + "\","
	                + "\"description\":\"\","
	                + "\"type\":\"Local\","
	                + "\"parameterType\":\"Alphanumeric\","
	                + "\"moduleId\":0,"
	                + "\"defaultValue\":\"\","
	                + "\"protected\":false,"
	                + "\"unique\":false,"
	                + "\"concat\":\"\","
	                + "\"paramAttributes\":{"
	                + "\"maxLength\":0,"
	                + "\"minLength\":0,"
	                + "\"prefix\":\"\","
	                + "\"suffix\":\"\"},"
	                + "\"projectId\":3"
	                + "}";

	        Response response = given()
	                .header("Content-Type", "application/json")
	                .header("Authorization", "Bearer " + accessToken)
	                .body(requestBody)
	                .when()
	                .post(ConfiReader.get("createparaendpoint"))
	                .then()
	                .statusCode(200)
	                .extract()
	                .response();

	        int createdId = response.jsonPath().getInt("id");
	        String logMsg = "✅ Created Parameter Response: " + response.getBody().asString();
	        System.out.println("✅ Created Parameter ID: " + createdId);
	        System.out.println(logMsg);
	        logToFile("✅ Created Parameter ID: " + createdId);
	        logToFile(logMsg);

	        return createdId;
	    }

	    public static void deleteParameter(int id) {
	        String requestBody = "{"
	                + "\"ids\":[" + id + "],"
	                + "\"projectId\":3,"
	                + "\"isDelete\":true"
	                + "}";

	        Response response = given()
	                .header("Content-Type", "application/json")
	                .header("Authorization", "Bearer " + accessToken)
	                .body(requestBody)
	                .when()
	                .patch(ConfiReader.get("deleteparaendpoint"))
	                .then()
	                .statusCode(200)
	                .extract()
	                .response();

	        String logMsg = "✅ Delete Parameter Response: " + response.getBody().asString();
	        System.out.println(logMsg);
	        logToFile(logMsg);
	    }
	    
}
