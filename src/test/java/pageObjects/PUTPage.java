package pageObjects;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import Utilities.ConfigReader;
import Utilities.LoggerLoad;

public class PUTPage {

    private static final String UPDATE_USER_ENDPOINT = "/uap/updateuser/{userId}";

  
    public Response updateUser(String userId, Object updateUserPayload) {
        LoggerLoad.info("🔹 Sending PUT request to update user with ID: " + userId);
        LoggerLoad.info("🔹 Request Payload: " + updateUserPayload.toString());

        // Create a fresh request spec to avoid conflicts
        RequestSpecification requestSpec = RestAssured.given()
                .baseUri(RestAssured.baseURI) // Use base URI from BaseTest
                .auth().preemptive().basic(ConfigReader.getUsername(), ConfigReader.getPassword()) // Authentication
                .header("Content-Type", "application/json") // Ensure JSON content type
                .pathParam("userId", userId); // Set only userId

        // Perform PUT request
        Response response = requestSpec
                .body(updateUserPayload)
                .when()
                .put(UPDATE_USER_ENDPOINT)
                .then()
                .extract()
                .response();

        LoggerLoad.info("Response Status Code: " + response.getStatusCode());
        LoggerLoad.info("Response Body: " + response.getBody().asString());

        return response;
    }

   
    public Response updateUserWithInvalidData(String userId, Object invalidUserPayload) {
        LoggerLoad.info("Sending PUT request with invalid data for user ID: " + userId);
        LoggerLoad.info("Request Payload: " + invalidUserPayload.toString());

        // Create a fresh request spec to avoid conflicts
        RequestSpecification requestSpec = RestAssured.given()
                .baseUri(RestAssured.baseURI) // Use base URI from BaseTest
                .auth().preemptive().basic(ConfigReader.getUsername(), ConfigReader.getPassword()) // Authentication
                .header("Content-Type", "application/json") // Ensure JSON content type
                .pathParam("userId", userId); // Set only userId

        // Perform PUT request
        Response response = requestSpec
                .body(invalidUserPayload)
                .when()
                .put(UPDATE_USER_ENDPOINT)
                .then()
                .extract()
                .response();

        LoggerLoad.info("Response Status Code: " + response.getStatusCode());
        LoggerLoad.info("Response Body: " + response.getBody().asString());

        return response;
    }
}
