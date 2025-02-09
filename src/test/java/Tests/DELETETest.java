package Tests;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;
import pageObjects.DELETEPage;
import Utilities.LoggerLoad;
import Utilities.TestDataProvider;
import org.json.simple.JSONObject;
import java.io.File;

public class DELETETest {

    DELETEPage deletePage = new DELETEPage();
    private static final String DELETE_USER_SCHEMA_PATH = "src/test/resources/schemas/DeleteUserSchema.json";

    @Test(priority=8, dataProvider = "DeleteData", dataProviderClass = TestDataProvider.class)
    public void testDeleteUser(JSONObject testData) {
        String testCaseName = (String) testData.get("test_case");

        if (!testCaseName.startsWith("Delete User")) {
            throw new SkipException("Skipping non-DELETE test: " + testCaseName);
        }

        LoggerLoad.info("Running Test Case: " + testCaseName);

        Response response;
        int expectedStatusCode;
        String expectedStatusLine;

        if (testCaseName.contains("Positive")) {
            // DELETE by First Name 
            String userFirstName = (String) testData.get("user_first_name");
            if (userFirstName == null || userFirstName.isEmpty()) {
                throw new IllegalArgumentException("user_first_name is missing in test data!");
            }

            // Perform GET request before DELETE to confirm user exists
            LoggerLoad.info("Performing GET request before DELETE for user: " + userFirstName);
            Response getUserResponse = deletePage.getUserByFirstName(userFirstName);
            int getUserStatusCode = getUserResponse.getStatusCode();

            LoggerLoad.info("GET Response Status Code: " + getUserStatusCode);
            LoggerLoad.info("GET Response Body: " + getUserResponse.getBody().asString());

            if (getUserStatusCode != 200) {
                LoggerLoad.error("User not found! Skipping DELETE for: " + userFirstName);
                Assert.fail("GET request failed. User '" + userFirstName + "' does not exist.");
            }

            // Proceed with DELETE only if GET is successful
            LoggerLoad.info("Sending DELETE request for user: " + userFirstName);
            response = deletePage.deleteUserByFirstName(userFirstName);
            expectedStatusCode = 200;
            expectedStatusLine = "200 OK";

            // Verify user is actually deleted
            LoggerLoad.info("Performing GET request after DELETE to verify user deletion: " + userFirstName);
            Response getAfterDelete = deletePage.getUserByFirstName(userFirstName);
            Assert.assertEquals(getAfterDelete.getStatusCode(), 404, "User still exists after DELETE!");
            LoggerLoad.info("Verified user is successfully deleted!");

        } else {
            // DELETE by Invalid User ID (Negative Case)
            String invalidUserId = (String) testData.get("user_id");
            if (invalidUserId == null || invalidUserId.isEmpty()) {
                throw new IllegalArgumentException("user_id is missing in test data!");
            }

            LoggerLoad.info("Sending DELETE request with INVALID User ID: " + invalidUserId);
            response = deletePage.deleteUserByInvalidId(invalidUserId);

            // sometimes i got 400 and 404 so we include both
            int statusCode = response.getStatusCode();
            if (statusCode == 400) {
                expectedStatusCode = 400;
                expectedStatusLine = "400 Bad Request";
                LoggerLoad.info("Expected failure: Bad Request due to invalid User ID format.");
            } else if (statusCode == 404) {
                expectedStatusCode = 404;
                expectedStatusLine = "404 Not Found";
                LoggerLoad.info("Expected failure: User does not exist.");
            } else {
                Assert.fail("Unexpected Status Code! Expected 400 or 404 but got: " + statusCode);
                return;
            }
        }

        int statusCode = response.getStatusCode();
        String statusLine = response.getStatusLine().trim().replace("HTTP/1.1 ", "");
        String responseBody = response.getBody().asString();

        LoggerLoad.info("Response Status Code: " + statusCode);
        LoggerLoad.info("Response Status Line: " + statusLine);
        LoggerLoad.info("Response Headers: " + response.getHeaders().asList());
        LoggerLoad.info("Response Body: " + responseBody);

        
        Assert.assertEquals(statusCode, expectedStatusCode, "Unexpected Status Code!");

        
        Assert.assertTrue(statusLine.contains(String.valueOf(expectedStatusCode)),
        	    "Unexpected Status Line! Expected: " + expectedStatusCode + " but got: " + statusLine);

        
        Assert.assertEquals(response.getHeader("Content-Type"), "application/json", "Invalid Content-Type!");
        Assert.assertNotNull(response.getHeader("Server"), "Server header is missing!");

        if (testCaseName.contains("Positive")) {
            LoggerLoad.info("Expected Success for Test: " + testCaseName);

            
            Assert.assertEquals(response.jsonPath().getString("status"), "Success", "Status mismatch!");
            Assert.assertEquals(response.jsonPath().getString("message"), "User is deleted successfully", "Message mismatch!");

            
            response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(new File(DELETE_USER_SCHEMA_PATH)));

        } else {
            LoggerLoad.info("Expected Failure for Test: " + testCaseName);

            
            String actualErrorMessage = response.jsonPath().getString("message");
            Assert.assertTrue(actualErrorMessage.contains("Invalid User ID") || 
                              actualErrorMessage.contains("Bad Request") || 
                              actualErrorMessage.contains("User does not exist"),
                "Error message mismatch! Expected 'Invalid User ID' or 'User does not exist' but got: " + actualErrorMessage);

           
            response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(new File(DELETE_USER_SCHEMA_PATH)));
        }

        LoggerLoad.info("Test Completed: " + testCaseName);
    }
}
