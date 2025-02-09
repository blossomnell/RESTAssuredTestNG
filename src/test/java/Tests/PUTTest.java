package Tests;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;
import pageObjects.PUTPage;
import Utilities.LoggerLoad;
import Utilities.TestDataProvider;
import org.json.simple.JSONObject;
import java.io.File;

public class PUTTest {

    PUTPage putPage = new PUTPage();
    private static final String UPDATE_USER_SCHEMA_PATH = "src/test/resources/schemas/UpdateUserSchema.json";

    @Test(priority=7, dataProvider = "PutData", dataProviderClass = TestDataProvider.class)
    public void testUpdateUser(JSONObject testData) {
        String testCaseName = (String) testData.get("test_case");

        // Ensure only PUT-related tests run
        if (!testCaseName.startsWith("Update User")) {
            throw new SkipException("Skipping non-PUT test: " + testCaseName);
        }

        LoggerLoad.info("Running Test Case: " + testCaseName);

        
        String userId = (String) testData.get("user_id");
        if (userId == null) {
            throw new IllegalArgumentException("user_id is missing in test data!");
        }

        Response response;
        if (testCaseName.contains("Positive")) {
            response = putPage.updateUser(userId, testData);
        } else {
            response = putPage.updateUserWithInvalidData(userId, testData);
        }

        int statusCode = response.getStatusCode();
        String statusLine = response.getStatusLine().trim(); // Trim to handle extra spaces
        String responseBody = response.getBody().asString();

        
        LoggerLoad.info("Response Status Code: " + statusCode);
        LoggerLoad.info("Response Status Line: " + statusLine);
        LoggerLoad.info("Response Headers: " + response.getHeaders().asList());
        LoggerLoad.info("Response Body: " + responseBody);

        if (testCaseName.contains("Positive")) {
            LoggerLoad.info("Expected Success for Test: " + testCaseName);

            
            Assert.assertEquals(statusCode, 200, "Expected 200 OK, but got: " + statusCode);

            
            Assert.assertTrue(statusLine.contains("200"), "Unexpected Status Line: " + statusLine);

            
            Assert.assertEquals(response.getHeader("Content-Type"), "application/json", "Invalid Content-Type");
            Assert.assertNotNull(response.getHeader("Server"), "Server header is missing");

            
            Assert.assertEquals(response.jsonPath().getString("user_first_name"), testData.get("user_first_name"), "First name mismatch!");
            Assert.assertEquals(response.jsonPath().getString("user_last_name"), testData.get("user_last_name"), "Last name mismatch!");
            Assert.assertEquals(response.jsonPath().getString("user_email_id"), testData.get("user_email_id"), "Email mismatch!");

            
            response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(new File(UPDATE_USER_SCHEMA_PATH)));

        } else if (testCaseName.contains("Negative")) {
            LoggerLoad.info("Expected Failure for Test: " + testCaseName);

            
            Assert.assertEquals(statusCode, 400, "Expected 400 BAD_REQUEST, but got: " + statusCode);

            
            Assert.assertTrue(statusLine.contains("400"), "Unexpected Status Line: " + statusLine);

            
            String actualErrorMessage = response.jsonPath().getString("message");
            Assert.assertTrue(actualErrorMessage.toLowerCase().contains("user first name is mandatory"),
                    "Error message mismatch! Expected part of: 'User First Name is mandatory...' but found: " + actualErrorMessage);
        }

        LoggerLoad.info("Test Completed: " + testCaseName);
    }
}
