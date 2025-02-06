package pageObjects;

import baseTest.BaseTest;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import Utilities.LoggerLoad;
import java.util.Map;

public class POSTPage {
    private static final String CREATE_USER_ENDPOINT = "/uap/createusers";

    @SuppressWarnings("unchecked")
    public Response createUser(Object payload) {
        // Ensure the payload is a valid Map before converting to JSONObject
        JSONObject requestBody = new JSONObject();

        if (payload instanceof Map<?, ?>) {
            Map<String, Object> safePayload = (Map<String, Object>) payload;
            requestBody.putAll(safePayload); // Safe conversion
        } else {
            throw new IllegalArgumentException("Invalid payload: Expected a Map<String, Object>");
        }

        // Logging request details
        LoggerLoad.info("🔹 Creating User with Payload: " + requestBody.toJSONString());

        // Sending POST request using BaseTest.requestSpec
        Response response = BaseTest.requestSpec
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post(CREATE_USER_ENDPOINT)
                .then()
                .extract()
                .response();

        // Logging response details
        LoggerLoad.info("📌 Response Status Code: " + response.getStatusCode());
        LoggerLoad.info("📌 Response Body: " + response.asString());

        // Handling Negative Scenario
        if (response.getStatusCode() != 201) {
            LoggerLoad.error("❌ User creation failed. Status Code: " + response.getStatusCode());
            if (response.getStatusCode() == 400) {
                LoggerLoad.error("🚨 Expected Negative Scenario: Invalid input data.");
            }
        }

        return response;
    }
}
