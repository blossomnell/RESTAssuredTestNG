package pageObjects;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import java.util.Map;

public class PUTPage {
    private static final String UPDATE_USER_ENDPOINT = "/uap/updateuser/{userId}";

    public Response updateUser(String userId, Object payload) {
        // Convert payload to JSON object to ensure JSON structure
        JSONObject requestBody = new JSONObject((Map) payload);

        // Logging request details
        System.out.println("🔹 Sending PUT Request...");
        System.out.println("🔹 Updating User with ID: " + userId);
        System.out.println("🔹 Request Payload: " + requestBody.toJSONString());

        // Sending PUT request
        Response response = RestAssured
                .given()
                .contentType("application/json")
                .pathParam("userId", userId)
                .body(requestBody)
                .when()
                .put(UPDATE_USER_ENDPOINT)
                .then()
                .extract()
                .response();

        // Logging response details
        System.out.println("✅ Response Status Code: " + response.getStatusCode());
        System.out.println("✅ Response Body: " + response.asString());

        return response;
    }
}
