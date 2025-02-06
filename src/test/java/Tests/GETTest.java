package Tests;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;
import pageObjects.GETPage;
import Utilities.LoggerLoad;
import Utilities.TestDataProvider;
import org.json.simple.JSONObject;

import java.io.File;

public class GETTest {

    GETPage getPage = new GETPage();
    private static final String GET_USER_SCHEMA_PATH = "src/test/resources/schemas/GetUserSchema.json";

    @Test(dataProvider = "GetData", dataProviderClass = TestDataProvider.class)
    public void testGetUser(JSONObject testData) {
        // Extract test case name
        String testCaseName = (String) testData.get("test_case");

        // Skip non-GET test cases
        if (!testCaseName.startsWith("Get User")) {
            throw new SkipException("Skipping non-GET test: " + testCaseName);
        }

        LoggerLoad.info("Running Test Case: " + testCaseName);

        // Extract user first name or invalid endpoint
        String userFirstName = (String) testData.get("user_first_name");
        String wrongEndpoint = (String) testData.get("wrong_endpoint");

        // Send GET request
        Response response;
        if (userFirstName != null) {
            response = getPage.getUserByFirstName(userFirstName);
        } else if (wrongEndpoint != null) {
            response = getPage.getUserWithWrongEndpoint(wrongEndpoint);
        } else {
            throw new IllegalArgumentException("Invalid test data: Both user_first_name and wrong_endpoint are null.");
        }

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
            
            // Status Code Validation
            Assert.assertEquals(statusCode, 200, "Expected 200 OK, but got: " + statusCode);
            
            // Simplified Status Line Validation
            Assert.assertTrue(statusLine.contains("200"), "Status Line Mismatch! Found: " + statusLine);
            
            // Header Validations
            Assert.assertEquals(response.getHeader("Content-Type"), "application/json",
                    "Unexpected Content-Type in response");
            Assert.assertNotNull(response.getHeader("Server"), "Server header is missing");

            // Response Body Validations
            Assert.assertNotNull(responseBody, "Response body is empty!");
            Assert.assertTrue(responseBody.contains("\"user_first_name\":\"" + userFirstName + "\""),
                    "Expected user details in response but got: " + responseBody);

            // JSON Schema Validation
            response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(new File(GET_USER_SCHEMA_PATH)));

        } else if (testCaseName.contains("Negative")) {
            LoggerLoad.info("Expected Failure for Test: " + testCaseName);
            Assert.assertNotEquals(statusCode, 200, "Expected failure but received 200 OK!");
            Assert.assertTrue(statusCode == 404 || responseBody.toLowerCase().contains("not found") 
                    || responseBody.toLowerCase().contains("error"),
                    "Expected failure message in response but got: " + responseBody);
        }

        LoggerLoad.info("Test Completed: " + testCaseName);
    }
}
