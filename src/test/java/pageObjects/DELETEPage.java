package pageObjects;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import Utilities.ConfigReader;
import Utilities.LoggerLoad;

public class DELETEPage {

    private static final String DELETE_USER_ENDPOINT = "/uap/deleteuser/username/{userFirstName}";
    private static final String GET_USER_ENDPOINT = "/uap/users/username/{userFirstName}"; 

    // Positive Scenario
    public Response deleteUserByFirstName(String userFirstName) {
        LoggerLoad.info("Checking if user exists before deletion: " + userFirstName);
     
        Response getUserResponse = getUserByFirstName(userFirstName);
        if (getUserResponse.getStatusCode() != 200) {
            LoggerLoad.error("User not found before DELETE! Skipping delete operation.");
            return getUserResponse; 
        }

        LoggerLoad.info("Sending DELETE request for user: " + userFirstName);
       
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

    // Negative Scenario
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

    // this is the actual method that sends request to API. 
    //the first GET calls the 2nd method to check whether user exists before DELETE 
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
