package pageObjects;

import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import baseTest.BaseTest; 

public class APIChainingPage {

    private static final String CREATE_USER_ENDPOINT = "/uap/createusers";
    private static final String GET_USER_ENDPOINT = "/uap/users/username/{firstName}"; 
    private static final String UPDATE_USER_ENDPOINT = "/uap/updateuser/{userId}";  
    private static final String DELETE_USER_ENDPOINT = "/uap/deleteuser/username/{firstName}"; 

 
    public Response createUser(Object payload) {
        Response response = given()
                .spec(BaseTest.requestSpec) //avoids baseURI, authentication in every test, evry rqst follows the same config
                .contentType("application/json")
                .body(payload)
                .when()
                .post(CREATE_USER_ENDPOINT)
                .then()
                .extract()
                .response();

        System.out.println("User Created Successfully: " + response.asString());
        return response;
    }

    public Response getUser(String firstName) {
        Response response = given()
                .spec(BaseTest.requestSpec)  
                .pathParam("firstName", firstName)  
                .when()
                .get(GET_USER_ENDPOINT)  
                .then()
                .extract()
                .response();

        System.out.println("User Retrieved Successfully: " + response.asString());
        return response;
    }


    
    public Response updateUser(String userId, Object payload) {
        Response response = given()
                .spec(BaseTest.requestSpec) 
                .contentType("application/json")
                .pathParam("userId", userId)
                .body(payload)
                .when()
                .put(UPDATE_USER_ENDPOINT)
                .then()
                .extract()
                .response();

        System.out.println("User Updated Successfully: " + response.asString());
        return response;
    }

    public Response deleteUser(String firstName) {
        Response response = given()
                .spec(BaseTest.requestSpec)
                .pathParam("firstName", firstName)  
                .when()
                .delete(DELETE_USER_ENDPOINT)
                .then()
                .extract()
                .response();

        System.out.println("User Deleted Successfully: " + response.asString());
        return response;
    }

}
