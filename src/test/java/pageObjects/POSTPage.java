package pageObjects;

import baseTest.BaseTest;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import Utilities.LoggerLoad;
import java.util.Map;

public class POSTPage {
    private static final String CREATE_USER_ENDPOINT = "/uap/createusers";

    public Response createUser(Map<String, Object> payload) {
        // Convert Map to JSONObject directly
        JSONObject requestBody = new JSONObject(payload);

        // Logging request details
        LoggerLoad.info("Creating User with Payload: " + requestBody.toJSONString());

        // Sending POST request
        Response response = BaseTest.requestSpec
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post(CREATE_USER_ENDPOINT)
                .then()
                .extract()
                .response();

        // Logging response details
        LoggerLoad.info("Response Status Code: " + response.getStatusCode());
        LoggerLoad.info("Response Body: " + response.asString());

        return response;
    }

    
    public Response createUserWithInvalidData(Map<String, Object> invalidPayload) {
        // Convert Map to JSONObject directly
        JSONObject requestBody = new JSONObject(invalidPayload);

        // Logging request details
        LoggerLoad.info("Attempting Invalid User Creation with Payload: " + requestBody.toJSONString());

        // Sending POST request
        Response response = BaseTest.requestSpec
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post(CREATE_USER_ENDPOINT)
                .then()
                .extract()
                .response();

        // Logging response details
        LoggerLoad.info("Response Status Code: " + response.getStatusCode());
        LoggerLoad.info("Response Body: " + response.asString());

        // Handling Negative Scenario Logging
        if (response.getStatusCode() != 201) {
            LoggerLoad.error("User creation failed. Status Code: " + response.getStatusCode());
            LoggerLoad.error("Expected Negative Scenario: Invalid input data.");
        }

        return response;
    }
}
