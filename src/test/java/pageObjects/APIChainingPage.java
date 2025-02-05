package pageObjects;

import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class APIChainingPage {

    private static final String CREATE_USER_ENDPOINT = "/uap/createusers";
    private static final String GET_USER_ENDPOINT = "/uap/getuser/{firstName}";
    private static final String UPDATE_USER_ENDPOINT = "/uap/updateuser/{userId}";
    private static final String DELETE_USER_ENDPOINT = "/uap/deleteuser/{firstName}";

    // ✅ Step 1: Create User
    public Response createUser(Object payload) {
        System.out.println("📌 Sending Create User Request: " + payload.toString());
        
        Response response = given()
                .contentType("application/json")
                .body(payload)
                .when()
                .post(CREATE_USER_ENDPOINT)
                .then()
                .extract()
                .response();

        if (response == null || response.getStatusCode() != 201) {
            System.out.println("❌ Failed to create user! Response: " + response);
        } else {
            System.out.println("✅ User Created Successfully: " + response.asString());
        }

        return response;
    }

    // ✅ Step 2: Get User by First Name
    public Response getUser(String firstName) {
        if (firstName == null || firstName.isEmpty()) {
            throw new IllegalArgumentException("❌ First Name cannot be null or empty!");
        }

        System.out.println("📌 Fetching User with First Name: " + firstName);
        
        Response response = given()
                .pathParam("firstName", firstName)
                .when()
                .get(GET_USER_ENDPOINT)
                .then()
                .extract()
                .response();

        if (response == null || response.getStatusCode() != 200) {
            System.out.println("❌ Failed to retrieve user! Response: " + response);
        } else {
            System.out.println("✅ User Retrieved Successfully: " + response.asString());
        }

        return response;
    }

    // ✅ Step 3: Update User by User ID (Last Name & ZipCode Only)
    public Response updateUser(String userId, Object payload) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("❌ User ID cannot be null or empty!");
        }
        if (payload == null) {
            throw new IllegalArgumentException("❌ Update Payload cannot be null!");
        }

        System.out.println("📌 Updating User with ID: " + userId + " | Payload: " + payload.toString());
        
        Response response = given()
                .contentType("application/json")
                .pathParam("userId", userId)
                .body(payload)
                .when()
                .put(UPDATE_USER_ENDPOINT)
                .then()
                .extract()
                .response();

        if (response == null || response.getStatusCode() != 200) {
            System.out.println("❌ Failed to update user! Response: " + response);
        } else {
            System.out.println("✅ User Updated Successfully: " + response.asString());
        }

        return response;
    }

    // ✅ Step 4: Delete User by First Name
    public Response deleteUser(String firstName) {
        if (firstName == null || firstName.isEmpty()) {
            throw new IllegalArgumentException("❌ First Name cannot be null or empty!");
        }

        System.out.println("📌 Deleting User with First Name: " + firstName);
        
        Response response = given()
                .pathParam("firstName", firstName)
                .when()
                .delete(DELETE_USER_ENDPOINT)
                .then()
                .extract()
                .response();

        if (response == null || response.getStatusCode() != 200) {
            System.out.println("❌ Failed to delete user! Response: " + response);
        } else {
            System.out.println("✅ User Deleted Successfully: " + response.asString());
        }

        return response;
    }
}
