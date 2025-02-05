package Tests;

import io.restassured.response.Response;

import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import Utilities.TestDataProvider;
import pageObjects.DELETEPage;
import pageObjects.POSTPage;
import baseTest.BaseTest;

public class DELETETest extends BaseTest {
    private DELETEPage deletePage;
    private POSTPage postPage;
    private String userId;

    @BeforeClass
    public void setupPage() {
        deletePage = new DELETEPage();
        postPage = new POSTPage();

        // Retrieve User ID dynamically
        userId = System.getProperty("user_id");

        if (userId == null || userId.isEmpty()) {
            System.out.println("⚠ No User ID found. Creating a new user...");

            // Create a new user and fetch user ID from response
            JSONObject newUserPayload = new JSONObject();
            newUserPayload.put("user_first_name", "DeleteUser");
            newUserPayload.put("user_last_name", "Test");
            newUserPayload.put("user_contact_number", "9998887770");
            newUserPayload.put("user_email_id", "deleteuser.test@example.com");

            Response createResponse = postPage.createUser(newUserPayload);
            userId = createResponse.jsonPath().getString("user_id");  // Fetch user_id dynamically

            System.out.println("✅ New User Created. ID: " + userId);
            System.setProperty("user_id", userId); // Store for future use
        } else {
            System.out.println("✅ Retrieved User ID for DELETE: " + userId);
        }
    }

    @Test(dataProvider = "NonChainingData", dataProviderClass = TestDataProvider.class)
    public void testDeleteUser(String testCase, Object payload) {
        System.out.println("🔹 Deleting user with ID: " + userId);

        // Send DELETE request
        Response response = deletePage.deleteUser(userId);

        // Assertions
        if (testCase.contains("Positive")) {
            Assert.assertEquals(response.getStatusCode(), 200, "✅ User should be deleted successfully!");
            System.out.println("✅ User successfully deleted: " + response.asString());
        } else if (testCase.contains("Negative")) {
            Assert.assertNotEquals(response.getStatusCode(), 200, "❌ User deletion should fail!");
            System.out.println("❌ Expected failure - Delete unsuccessful: " + response.asString());
        }
    }
}
