package pageObjects;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class GETPage {
    private static final String GET_USER_ENDPOINT = "/uap/getuser/{firstName}";

    public Response getUserByFirstName(String firstName) {
        System.out.println("Fetching User with First Name: " + firstName);
        return RestAssured
                .given()
                .pathParam("firstName", firstName)
                .when()
                .get(GET_USER_ENDPOINT)
                .then()
                .extract()
                .response();
    }
}