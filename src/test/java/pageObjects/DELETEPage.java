package pageObjects;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import Utilities.ConfigReader;
import Utilities.LoggerLoad;

public class DELETEPage {

    private static final String DELETE_USER_ENDPOINT = "/uap/deleteuser/username/{userFirstName}"; // ✅ Corrected DELETE endpoint
    private static final String GET_USER_ENDPOINT = "/uap/users/username/{userFirstName}"; // ✅ Corrected GET endpoint

    // DELETE by User First Name (Positive Scenario)
    public Response deleteUserByFirstName(String userFirstName) {
        LoggerLoad.info("Checking if user exists before deletion: " + userFirstName);

        // Step 1: Perform GET request before DELETE
        Response getUserResponse = getUserByFirstName(userFirstName);
        if (getUserResponse.getStatusCode() != 200) {
            LoggerLoad.error("User not found before DELETE! Skipping delete operation.");
            return getUserResponse; // Returning GET response to indicate failure
        }

        LoggerLoad.info("🗑Sending DELETE request for user: " + userFirstName);

        // Step 2: Perform DELETE request
        RequestSpecification requestSpec = RestAssured.given()
                .baseUri(RestAssured.baseURI)
                .auth().preemptive().basic(ConfigReader.getUsername(), ConfigReader.getPassword()) 
                .pathParam("userFirstName", userFirstName);

        Response response = requestSpec
                .when()
                .delete(DELETE_USER_ENDPOINT)
                .then()
                .extract()
                .response();

        LoggerLoad.info("Response Status Code: " + response.getStatusCode());
        LoggerLoad.info("Response Body: " + response.getBody().asString());

        if (response.getStatusCode() != 200) {
            LoggerLoad.error("DELETE request failed! Response: " + response.asString());
        }

        return response;
    }

    // DELETE by Invalid User ID (Negative Scenario)
    public Response deleteUserByInvalidId(String invalidUserFirstName) {
        LoggerLoad.info("Attempting to DELETE a non-existent user: " + invalidUserFirstName);

        RequestSpecification requestSpec = RestAssured.given()
                .baseUri(RestAssured.baseURI)
                .auth().preemptive().basic(ConfigReader.getUsername(), ConfigReader.getPassword()) 
                .pathParam("userFirstName", invalidUserFirstName);

        Response response = requestSpec
                .when()
                .delete(DELETE_USER_ENDPOINT)
                .then()
                .extract()
                .response();

        LoggerLoad.info("Response Status Code: " + response.getStatusCode());
        LoggerLoad.info("Response Body: " + response.getBody().asString());

        if (response.getStatusCode() == 400) {
            LoggerLoad.info("Expected failure: Bad Request due to invalid user ID format.");
        } else if (response.getStatusCode() == 404) {
            LoggerLoad.info("Expected failure: User does not exist.");
        } else {
            LoggerLoad.error("Unexpected response! Expected 400 or 404 but got: " + response.getStatusCode());
        }

        return response;
    }

    // GET user before DELETE (to confirm existence)
    public Response getUserByFirstName(String userFirstName) {
        LoggerLoad.info("Performing GET request for user: " + userFirstName);

        RequestSpecification requestSpec = RestAssured.given()
                .baseUri(RestAssured.baseURI)
                .auth().preemptive().basic(ConfigReader.getUsername(), ConfigReader.getPassword()) 
                .pathParam("userFirstName", userFirstName);

        Response response = requestSpec
                .when()
                .get(GET_USER_ENDPOINT)
                .then()
                .extract()
                .response();

        LoggerLoad.info("GET Response Status Code: " + response.getStatusCode());
        LoggerLoad.info("GET Response Body: " + response.getBody().asString());

        return response;
    }
}
