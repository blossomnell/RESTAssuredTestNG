package Tests;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pageObjects.APIChainingPage;
import Utilities.JsonDataReader;
import Utilities.LoggerLoad;

import java.util.HashMap;
import java.util.Map;

public class APIChainingTest {
    private APIChainingPage apiChainingPage;
    private String userId;
    private String firstName;

    @BeforeClass
    public void setupPage() {
        apiChainingPage = new APIChainingPage();
        LoggerLoad.info("API Chaining Test Suite Initialized.");
    }

    // Creating the User
    @Test(priority = 1)
    public void testCreateUser() {
        Map<String, Object> userData = JsonDataReader.getChainingTestData();
        LoggerLoad.info("Creating User with Data: " + userData);

        long startTime = System.currentTimeMillis();
        Response response = apiChainingPage.createUser(userData);
        long endTime = System.currentTimeMillis();

        
        Assert.assertEquals(response.getStatusCode(), 201, "User creation failed! Status: " + response.getStatusCode());

        
        Assert.assertTrue(response.getStatusLine().contains("201"), "Status Line Mismatch! Found: " + response.getStatusLine());

        
        Assert.assertNotNull(response.getHeader("Content-Type"), "Missing Content-Type header!");

        
        userId = response.jsonPath().getString("user_id");
        firstName = response.jsonPath().getString("user_first_name");
        Assert.assertNotNull(userId, "user_id is null!");
        Assert.assertNotNull(firstName, "firstName is null!");

        
        response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/CreateUserSchema.json"));

        LoggerLoad.info("User Created Successfully - ID: " + userId + ", Name: " + firstName);
        LoggerLoad.info("Response Time: " + (endTime - startTime) + " ms");
    }

    // Retrieving the user
    @Test(priority = 2, dependsOnMethods = "testCreateUser")
    public void testGetUser() {
        Assert.assertNotNull(firstName, "First Name is null before GET operation!");
        LoggerLoad.info("Fetching User Details for: " + firstName);

        long startTime = System.currentTimeMillis();
        Response response = apiChainingPage.getUser(firstName);
        long endTime = System.currentTimeMillis();

        
        Assert.assertEquals(response.getStatusCode(), 200, "User retrieval failed! Status: " + response.getStatusCode());

        
        response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/GetUserSchema.json"));

        LoggerLoad.info("User Retrieved: ID=" + response.jsonPath().getString("user_id") + ", Name=" + response.jsonPath().getString("user_first_name"));
        LoggerLoad.info("Response Time: " + (endTime - startTime) + " ms");
    }

    
    @Test(priority = 3, dependsOnMethods = "testGetUser")
    public void testUpdateUser() {
        Assert.assertNotNull(userId, "User ID is null before UPDATE operation!");
        Assert.assertNotNull(firstName, "First Name is null before UPDATE operation!");

        Map<String, Object> userData = JsonDataReader.getChainingTestData();
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("user_id", userId);
        updateData.put("user_first_name", firstName);
        updateData.put("user_email_id", userData.get("user_email_id"));
        updateData.put("user_contact_number", userData.get("user_contact_number"));

        
        String updatedLastName = userData.get("user_last_name") + "Updated";
        updateData.put("user_last_name", updatedLastName);

        
        Object addressObj = userData.get("userAddress");
        String expectedZipCode = null;

        if (addressObj instanceof Map<?, ?>) {
            @SuppressWarnings("unchecked")
            Map<String, Object> userAddress = (Map<String, Object>) addressObj;

            if (userAddress.containsKey("zipCode")) {
                String existingZipCode = userAddress.get("zipCode").toString();
                expectedZipCode = existingZipCode + "1";
                userAddress.put("zipCode", expectedZipCode);
            } else {
                throw new RuntimeException("zipCode key is missing in userAddress!");
            }
            updateData.put("userAddress", userAddress);
        } else {
            throw new RuntimeException("userAddress is missing or not in the correct format!");
        }

        LoggerLoad.info("Updating User ID: " + userId + " with Data: " + updateData);
        long startTime = System.currentTimeMillis();
        Response response = apiChainingPage.updateUser(userId, updateData);
        long endTime = System.currentTimeMillis();

        
        Assert.assertEquals(response.getStatusCode(), 200, "User update failed! Status: " + response.getStatusCode());

        
        response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/UpdateUserSchema.json"));

        String responseLastName = response.jsonPath().getString("user_last_name");
        String responseZipCode = response.jsonPath().getString("userAddress.zipCode");

        Assert.assertEquals(responseLastName, updatedLastName, "Last Name update mismatch!");
        Assert.assertEquals(responseZipCode, expectedZipCode, "Zip Code update mismatch!");

        LoggerLoad.info("User Updated Successfully: Last Name=" + responseLastName + ", ZipCode=" + responseZipCode);
        LoggerLoad.info("Response Time: " + (endTime - startTime) + " ms");
    }

    // Deleting the user
    @Test(priority = 4, dependsOnMethods = "testUpdateUser")
    public void testDeleteUser() {
        Assert.assertNotNull(firstName, "First Name is null before DELETE operation!");
        LoggerLoad.info("Deleting User: " + firstName);

        long startTime = System.currentTimeMillis();
        Response response = apiChainingPage.deleteUser(firstName);
        long endTime = System.currentTimeMillis();

        
        Assert.assertEquals(response.getStatusCode(), 200, "User deletion failed! Status: " + response.getStatusCode());

        
        response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/DeleteUserSchema.json"));

        LoggerLoad.info("User Deleted Successfully!");
        LoggerLoad.info("Response Time: " + (endTime - startTime) + " ms");
    }
}
