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

    @SuppressWarnings("unchecked")
	@Test(dataProvider = "PostData", dataProviderClass = TestDataProvider.class)
    public void testCreateUser(JSONObject testData) {
        String testCaseName = (String) testData.get("test_case");

        // Ensure only POST-related tests run
        if (!testCaseName.startsWith("Create User")) {
            throw new SkipException("Skipping non-POST test: " + testCaseName);
        }

        LoggerLoad.info("Running Test Case: " + testCaseName);

        // Send POST request
        Response response = postPage.createUser(testData);

        // Extract response details
        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();

        // Log response details
        LoggerLoad.info("Response Status Code: " + statusCode);
        LoggerLoad.info("Response Headers: " + response.getHeaders().asList());
        LoggerLoad.info("Response Body: " + responseBody);

        if (testCaseName.contains("Positive")) {
            LoggerLoad.info("Expected Success for Test: " + testCaseName);

            // **Status Code Validation (Simplified)**
            Assert.assertEquals(statusCode, 201, "Expected 201 Created, but got: " + statusCode);

            // **Header Validations**
            Assert.assertEquals(response.getHeader("Content-Type"), "application/json",
                    "Unexpected Content-Type in response");
            Assert.assertNotNull(response.getHeader("Server"), "Server header is missing");

            // ✔ **Data Validation**
            String userId = response.jsonPath().getString("user_id");
            String firstName = response.jsonPath().getString("user_first_name");

            Assert.assertNotNull(userId, "user_id is null!");
            Assert.assertNotNull(firstName, "user_first_name is null!");
            Assert.assertEquals(firstName, testData.get("user_first_name"),
                    "Mismatch in created user first name!");

            // **JSON Schema Validation (Only for Successful Requests)**
            response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(new File(CREATE_USER_SCHEMA_PATH)));

        } else if (testCaseName.contains("Negative")) {
            LoggerLoad.info("Expected Failure for Test: " + testCaseName);

            // **Status Code Validation**
            Assert.assertNotEquals(statusCode, 201, "Expected failure but received 201 Created!");

            // **Error Message Validation**
            Assert.assertTrue(responseBody.contains("status"),
                    "Expected error response with 'status' field, but got: " + responseBody);
            Assert.assertTrue(responseBody.contains("message"),
                    "Expected error response with 'message' field, but got: " + responseBody);
        }

        LoggerLoad.info("Test Completed: " + testCaseName);
    }
}
