package Tests;

import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pageObjects.PUTPage;
import pageObjects.POSTPage; // ✅ Added import for POSTPage
import Utilities.TestDataProvider;
import baseTest.BaseTest;

import java.util.Random; // ✅ Import for generating unique contact number

public class PUTTest extends BaseTest {
    private PUTPage putPage;
    private POSTPage postPage; // ✅ Added POSTPage instance
    private String userId;

    @BeforeClass
    public void setupPage() {
        putPage = new PUTPage();
        postPage = new POSTPage(); // ✅ Initialize postPage

        // Retrieve user ID dynamically or create a new user if missing
        userId = System.getProperty("user_id");

        if (userId == null || userId.isEmpty()) {
            System.out.println("⚠ No User ID found. Creating a new user...");

            // ✅ Create a new user and fetch user ID from response
            JSONObject newUserPayload = new JSONObject();
            newUserPayload.put("user_first_name", "AutoUser");
            newUserPayload.put("user_last_name", "Test");

            // 🔹 Generate a unique contact number dynamically
            String contactNumber = "123" + (1000000 + new Random().nextInt(9000000));
            newUserPayload.put("user_contact_number", contactNumber);

            newUserPayload.put("user_email_id", "autouser" + System.currentTimeMillis() + "@example.com");

            Response createResponse = postPage.createUser(newUserPayload); // ✅ Calling POSTPage correctly
            userId = createResponse.jsonPath().getString("user_id"); // Fetch user_id dynamically

            if (userId == null || userId.isEmpty()) {
                throw new IllegalStateException("❌ Failed to create user. User ID is null!");
            }

            System.out.println("✅ New User Created. ID: " + userId);
            System.setProperty("user_id", userId); // Store for future use
        } else {
            System.out.println("✔ Retrieved User ID for PUT: " + userId);
        }
    }

    @Test(dataProvider = "NonChainingData", dataProviderClass = TestDataProvider.class)
    public void testUpdateUser(String testCase, Object payload) {
        // ✅ Ensure payload is mutable
        JSONObject payloadObject = new JSONObject((java.util.Map) payload);

        // ✅ Inject user ID into payload
        payloadObject.put("user_id", userId);

        System.out.println("✔ Updating user with ID: " + userId);
        System.out.println("✔ Payload Sent: " + payloadObject.toJSONString());

        // ✅ Send PUT request
        Response response = putPage.updateUser(userId, payloadObject);

        // ✅ Assertions
        if (testCase.contains("Positive")) {
            Assert.assertEquals(response.getStatusCode(), 200, "✅ User should be updated successfully!");
            System.out.println("✅ User successfully updated: " + response.asString());
        } else if (testCase.contains("Negative")) {
            Assert.assertNotEquals(response.getStatusCode(), 200, "❌ User update should fail!");
            System.out.println("❌ Expected failure - Update unsuccessful: " + response.asString());
        }
    }
}
