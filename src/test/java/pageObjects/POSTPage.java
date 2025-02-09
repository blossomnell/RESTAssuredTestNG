package pageObjects;

import baseTest.BaseTest;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import Utilities.LoggerLoad;
import java.util.Map;

public class POSTPage {
    private static final String CREATE_USER_ENDPOINT = "/uap/createusers";

    public Response createUser(Map<String, Object> payload) {
    	//payload is passed as Map<string, object> and then we convert it into json before sending
    	//string-username object-realvalue payload is the data sent to API
        // we convert Map to JSONObject bcoz RESTassured need body in json format
        JSONObject requestBody = new JSONObject(payload);

        
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

        
        LoggerLoad.info("Response Status Code: " + response.getStatusCode());
        LoggerLoad.info("Response Body: " + response.asString());

        return response;
    }
   
    public Response createUserWithInvalidData(Map<String, Object> invalidPayload) {
      
        JSONObject requestBody = new JSONObject(invalidPayload);
    
        LoggerLoad.info("Attempting Invalid User Creation with Payload: " + requestBody.toJSONString());
       
        Response response = BaseTest.requestSpec
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post(CREATE_USER_ENDPOINT)
                .then()
                .extract()
                .response();
        
        LoggerLoad.info("Response Status Code: " + response.getStatusCode());
        LoggerLoad.info("Response Body: " + response.asString());

        
        if (response.getStatusCode() != 201) {
            LoggerLoad.error("User creation failed. Status Code: " + response.getStatusCode());
            LoggerLoad.error("Expected Negative Scenario: Invalid input data.");
        }

        return response;
    }
}
