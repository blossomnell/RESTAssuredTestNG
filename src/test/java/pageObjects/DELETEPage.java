package pageObjects;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class DELETEPage {
    private static final String DELETE_USER_ENDPOINT = "/uap/deleteuser/{userId}";

    public Response deleteUser(String userId) {
        System.out.println("Deleting User with ID: " + userId);
        return RestAssured
                .given()
                .pathParam("userId", userId)
                .when()
                .delete(DELETE_USER_ENDPOINT)
                .then()
                .extract()
                .response();
    }
}