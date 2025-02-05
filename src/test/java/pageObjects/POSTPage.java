package pageObjects;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import java.util.Map;

public class POSTPage {
    private static final String CREATE_USER_ENDPOINT = "/uap/createusers";

    public Response createUser(Object payload) {
        // Convert payload to JSON object if necessary
    	JSONObject requestBody = new JSONObject((Map) payload);


        // Logging request details
        System.out.println("🔹 Creating User with Payload: " + requestBody.toJSONString());

        // Sending POST request
        Response response = RestAssured
                .given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post(CREATE_USER_ENDPOINT)
                .then()
                .extract()
                .response();

        // Logging response details
        System.out.println("✅ Response Status Code: " + response.getStatusCode());
        System.out.println("✅ Response Body: " + response.asString());

        return response;
    }
}
