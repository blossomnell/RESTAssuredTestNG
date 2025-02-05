package Tests;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pageObjects.APIChainingPage;
import Utilities.JsonDataReader;

import java.util.Map;

public class APIChainingTest {
    private APIChainingPage apiChainingPage;
    private String userId;  // ✅ Store generated user_id
    private String firstName; // ✅ Store firstName for later steps

    @BeforeClass
    public void setupPage() {
        apiChainingPage = new APIChainingPage();
    }

    // ✅ Step 1: Create User (Store user_id and firstName)
    @Test(priority = 1)
    public void testCreateUser() {
        Map<String, Object> userData = JsonDataReader.getChainingTestData(); // ✅ Read user from JSON
        System.out.println("📌 Sending Create User Request: " + userData.toString());

        long startTime = System.currentTimeMillis(); // ✅ Track API Response Time
        Response response = apiChainingPage.createUser(userData);
        long endTime = System.currentTimeMillis();
        
        // ✅ Ensure API Call was Successful
        Assert.assertNotNull(response, "❌ Create User API Response is null!");
        Assert.assertEquals(response.getStatusCode(), 201, "❌ User creation failed! Response: " + response.asString());

        // ✅ Extract user_id and firstName
        userId = response.jsonPath().getString("user_id");
        firstName = response.jsonPath().getString("user_first_name");

        // ✅ Ensure the extracted values are not null or empty
        Assert.assertNotNull(userId, "❌ user_id is null! Response: " + response.asString());
        Assert.assertFalse(userId.isEmpty(), "❌ user_id is an empty string! Response: " + response.asString());

        Assert.assertNotNull(firstName, "❌ firstName is null! Response: " + response.asString());
        Assert.assertFalse(firstName.isEmpty(), "❌ firstName is an empty string! Response: " + response.asString());

        System.out.println("✅ User Created Successfully - ID: " + userId + ", Name: " + firstName);
        System.out.println("⏱ Response Time: " + (endTime - startTime) + " ms");
    }

    // ✅ Step 2: Get User by First Name
    @Test(priority = 2, dependsOnMethods = "testCreateUser")
    public void testGetUser() {
        Assert.assertNotNull(firstName, "❌ First Name is null before GET operation!");

        System.out.println("📌 Fetching User Details for: " + firstName);
        long startTime = System.currentTimeMillis();
        Response response = apiChainingPage.getUser(firstName);
        long endTime = System.currentTimeMillis();

        // ✅ Ensure API Call was Successful
        Assert.assertNotNull(response, "❌ Get User API Response is null!");
        Assert.assertFalse(response.asString().isEmpty(), "❌ Get User API Response is empty!");
        Assert.assertEquals(response.getStatusCode(), 200, "❌ User retrieval failed! Response: " + response.asString());
        Assert.assertTrue(response.asString().contains(firstName), "❌ Retrieved data doesn't match firstName! Expected: " + firstName + ", Response: " + response.asString());

        System.out.println("✅ User Retrieved Successfully: " + response.asString());
        System.out.println("⏱ Response Time: " + (endTime - startTime) + " ms");
    }

    // ✅ Step 3: Update User (Update only Last Name & Zip Code using user_id)
    @Test(priority = 3, dependsOnMethods = "testGetUser")
    public void testUpdateUser() {
        Assert.assertNotNull(userId, "❌ User ID is null before UPDATE operation!");

        // ✅ Get predefined update data (lastName & zipCode) and inject user_id
        Map<String, Object> updateData = JsonDataReader.getUpdateTestData();
        updateData.put("user_id", userId);  // ✅ Inject user_id dynamically

        System.out.println("📌 Updating User ID: " + userId + " with Data: " + updateData);
        long startTime = System.currentTimeMillis();
        Response response = apiChainingPage.updateUser(userId, updateData);
        long endTime = System.currentTimeMillis();

        // ✅ Ensure API Call was Successful
        Assert.assertNotNull(response, "❌ Update User API Response is null!");
        Assert.assertFalse(response.asString().isEmpty(), "❌ Update User API Response is empty!");
        Assert.assertEquals(response.getStatusCode(), 200, "❌ User update failed! Response: " + response.asString());

        Assert.assertTrue(response.asString().contains("UpdatedAppoopan"), "❌ Last Name update not found! Response: " + response.asString());
        Assert.assertTrue(response.asString().contains("99999"), "❌ ZipCode update not found! Response: " + response.asString());

        System.out.println("✅ User Updated Successfully: " + response.asString());
        System.out.println("⏱ Response Time: " + (endTime - startTime) + " ms");
    }

    // ✅ Step 4: Delete User by First Name
    @Test(priority = 4, dependsOnMethods = "testUpdateUser")
    public void testDeleteUser() {
        Assert.assertNotNull(firstName, "❌ First Name is null before DELETE operation!");

        System.out.println("📌 Deleting User with First Name: " + firstName);
        long startTime = System.currentTimeMillis();
        Response response = apiChainingPage.deleteUser(firstName);
        long endTime = System.currentTimeMillis();

        // ✅ Ensure API Call was Successful
        Assert.assertNotNull(response, "❌ Delete User API Response is null!");
        Assert.assertFalse(response.asString().isEmpty(), "❌ Delete User API Response is empty!");
        Assert.assertEquals(response.getStatusCode(), 200, "❌ User deletion failed! Response: " + response.asString());

        System.out.println("✅ User Deleted Successfully!");
        System.out.println("⏱ Response Time: " + (endTime - startTime) + " ms");
    }
}
