package pageObjects;

import baseTest.BaseTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import Utilities.LoggerLoad;

public class GETPage {
    private static final String GET_USER_ENDPOINT = "/uap/users/username/{firstName}";

    public Response getUserByFirstName(String firstName) {
        LoggerLoad.info("Sending GET request to retrieve user with First Name: " + firstName);

        Response response = BaseTest.requestSpec
                .pathParam("firstName", firstName)
                .when()
                .get(GET_USER_ENDPOINT)
                .then()
                .extract()
                .response();

        LoggerLoad.info("Response Status Code: " + response.getStatusCode());
        LoggerLoad.info("Response Body: " + response.getBody().asString());

        return response;
    }

    public Response getUserWithWrongEndpoint(String wrongEndpoint) {
        LoggerLoad.info("Sending GET request to wrong endpoint: " + wrongEndpoint);

        // Use RestAssured directly instead of BaseTest.requestSpec to prevent path parameter issues
        Response response = RestAssured.given()
                .when()
                .get(wrongEndpoint)
                .then()
                .extract()
                .response();

        LoggerLoad.info("Response Status Code: " + response.getStatusCode());
        LoggerLoad.info("Response Body: " + response.getBody().asString());

        return response;
    }
}
