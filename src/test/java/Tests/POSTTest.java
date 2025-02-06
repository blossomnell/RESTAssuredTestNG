package Tests;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;
import pageObjects.POSTPage;
import Utilities.LoggerLoad;
import Utilities.TestDataProvider;
import org.json.simple.JSONObject;

import java.io.File;

public class POSTTest {

    POSTPage postPage = new POSTPage();
    private static final String CREATE_USER_SCHEMA_PATH = "src/test/resources/schemas/CreateUserSchema.json";

    @Test(dataProvider = "PostData", dataProviderClass = TestDataProvider.class)
    public void testCreateUser(JSONObject testData) {
        // Debugging: Print received test data
        System.out.println("Using Test Data: " + testData.toJSONString());

        String testCaseName = (String) testData.get("test_case");

        // Ensure only POST-related tests run
        if (!testCaseName.startsWith("Create User")) {
            throw new SkipException("Skipping non-POST test: " + testCaseName);
        }

        LoggerLoad.info("Running Test Case: " + testCaseName);

        // Send POST request
        Response response = postPage.createUser(testData);
        int statusCode = response.getStatusCode();
        String statusLine = response.getStatusLine();
        String responseBody = response.getBody().asString();

        // Logging response details
        LoggerLoad.info("Response Status Code: " + statusCode);
        LoggerLoad.info("Response Status Line: " + statusLine);
        LoggerLoad.info("Response Headers: " + response.getHeaders().asList());
        LoggerLoad.info("Response Body: " + responseBody);

        if (testCaseName.contains("Positive")) {
            LoggerLoad.info("Expected Success for Test: " + testCaseName);
            Assert.assertEquals(statusCode, 201, "Expected 201 Created, but got: " + statusCode);
            
            // Simplified Status Line Validation
            Assert.assertTrue(statusLine.contains("201"), "Status Line Mismatch! Found: " + statusLine);
            
            // Header Validations
            Assert.assertEquals(response.getHeader("Content-Type"), "application/json",
                    "Unexpected Content-Type in response");
            Assert.assertNotNull(response.getHeader("Server"), "Server header is missing");

            // Response Body Validations
            Assert.assertNotNull(response.jsonPath().getString("user_id"), "user_id is null!");
            Assert.assertNotNull(response.jsonPath().getString("user_first_name"), "firstName is null!");

            // JSON Schema Validation
            response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(new File(CREATE_USER_SCHEMA_PATH)));

        } else if (testCaseName.contains("Negative")) {
            LoggerLoad.info("Expected Failure for Test: " + testCaseName);
            Assert.assertNotEquals(statusCode, 201, "Expected failure but received 201 Created!");
            Assert.assertTrue(responseBody.contains("user FirstName is mandatory"),
                    "Expected error message for invalid first name, but got: " + responseBody);
        }

        LoggerLoad.info("Test Completed: " + testCaseName);
    }
}
