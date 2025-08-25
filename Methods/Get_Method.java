package Methods;

import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
public class Get_Method {
	
	public static void main(String[] args) {

        
        String baseURI = "https://reqres.in/api/users?page=2";

        
        Response response = given()
        		 .header("x-api-key", "<your_api_key>")  
                 .when()
                 .get(baseURI);
                                

        
        System.out.println("Status Code: " + response.getStatusCode());

        
        System.out.println("Response Body: " + response.getBody().asString());
    }

}
