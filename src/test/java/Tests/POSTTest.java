package Tests;

import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pageObjects.POSTPage;
import Utilities.TestDataProvider;
import baseTest.BaseTest;

import java.util.Random; // ✅ Import for generating random contact number

public class POSTTest extends BaseTest {
    private POSTPage postPage;

    @BeforeClass
    public void setupPage() {
        postPage = new POSTPage();
    }

    @Test(dataProvider = "NonChainingData", dataProviderClass = TestDataProvider.class)
    public void testCreateUser(String testCase, Object payload) {
        // ✅ Ensure payload is mutable
        JSONObject payloadObject = new JSONObject((java.util.Map) payload);

        // 🔹 Generate a unique contact number dynamically
        String contactNumber = "123" + (1000000 + new Random().nextInt(9000000));
        payloadObject.put("user_contact_number", contactNumber);

        // 🔹 Generate a unique email dynamically
        String uniqueEmail = "user" + System.currentTimeMillis() + "@example.com";
        payloadObject.put("user_email_id", uniqueEmail);

        System.out.println("✔ Creating User with Payload: " + payloadObject.toJSONString());

        // ✅ Send POST request
        Response response = postPage.createUser(payloadObject);

        System.out.println("✔ Response Status Code: " + response.getStatusCode());
        System.out.println("✔ Response Body: " + response.asString());

        // ✅ Assertions
        if (testCase.contains("Positive")) {
            Assert.assertEquals(response.getStatusCode(), 201, "✅ User should be created successfully!");
            System.out.println("✅ User Created Successfully!");
        } else if (testCase.contains("Negative")) {
            Assert.assertNotEquals(response.getStatusCode(), 201, "❌ User creation should fail!");
            System.out.println("❌ Expected Failure - User creation unsuccessful.");
        }
    }
}
