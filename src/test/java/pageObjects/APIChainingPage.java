package pageObjects;

import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import baseTest.BaseTest; // ✅ Import BaseTest to get crumbToken

public class APIChainingPage {

    private static final String CREATE_USER_ENDPOINT = "/uap/createusers";
    private static final String GET_USER_ENDPOINT = "/uap/getuser/{firstName}";
    private static final String UPDATE_USER_ENDPOINT = "/uap/updateuser/{userId}";
    private static final String DELETE_USER_ENDPOINT = "/uap/deleteuser/{firstName}";

    // ✅ Step 1: Create User
    public Response createUser(Object payload) {
        if (payload == null) {
            throw new IllegalArgumentException("❌ Create User Payload cannot be null!");
        }

        Response response = given()
                .header("Crumb", getCrumbToken()) // ✅ Fetch crumb dynamically
                .contentType("application/json")
                .body(payload)
                .when()
                .post(CREATE_USER_ENDPOINT)
                .then()
                .extract()
                .response();

        System.out.println("✅ User Created: " + response.asString());
        return response;
    }

    // ✅ Step 2: Get User by First Name
    public Response getUser(String firstName) {
        if (firstName == null || firstName.isEmpty()) {
            throw new IllegalArgumentException("❌ First Name cannot be null or empty!");
        }

        Response response = given()
                .header("Crumb", getCrumbToken())
                .pathParam("firstName", firstName)
                .when()
                .get(GET_USER_ENDPOINT)
                .then()
                .extract()
                .response();

        System.out.println("✅ User Retrieved: " + response.asString());
        return response;
    }

    // ✅ Step 3: Update User by User ID
    public Response updateUser(String userId, Object payload) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("❌ User ID cannot be null or empty!");
        }
        if (payload == null) {
            throw new IllegalArgumentException("❌ Update Payload cannot be null!");
        }

        Response response = given()
                .header("Crumb", getCrumbToken())
                .contentType("application/json")
                .pathParam("userId", userId)
                .body(payload)
                .when()
                .put(UPDATE_USER_ENDPOINT)
                .then()
                .extract()
                .response();

        System.out.println("✅ User Updated: " + response.asString());
        return response;
    }

    // ✅ Step 4: Delete User by First Name
    public Response deleteUser(String firstName) {
        if (firstName == null || firstName.isEmpty()) {
            throw new IllegalArgumentException("❌ First Name cannot be null or empty!");
        }

        Response response = given()
                .header("Crumb", getCrumbToken())
                .pathParam("firstName", firstName)
                .when()
                .delete(DELETE_USER_ENDPOINT)
                .then()
                .extract()
                .response();

        System.out.println("✅ User Deleted: " + response.asString());
        return response;
    }

    // ✅ Helper Method: Get Crumb Token
    private String getCrumbToken() {
        if (BaseTest.crumbToken == null || BaseTest.crumbToken.isEmpty()) {
            throw new IllegalStateException("❌ Crumb Token is missing! Ensure it is set in BaseTest.");
        }
        return BaseTest.crumbToken;
    }
}
