package Tests;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pageObjects.APIChainingPage;
import Utilities.JsonDataReader;

import java.util.HashMap;
import java.util.Map;

public class APIChainingTest {
    private APIChainingPage apiChainingPage;
    private String userId;  
    private String firstName; 
     
    @BeforeClass
    public void setupPage() {
        apiChainingPage = new APIChainingPage();
    }

    //  Creating the User
    @Test(priority = 1)
    public void testCreateUser() {
        Map<String, Object> userData = JsonDataReader.getChainingTestData(); 
        System.out.println("Creating User with Data: " + userData);

        long startTime = System.currentTimeMillis();
        Response response = apiChainingPage.createUser(userData);
        long endTime = System.currentTimeMillis();

        // ✅ Validate API response
        Assert.assertNotNull(response, "API Response is null!");
        Assert.assertEquals(response.getStatusCode(), 201, "User creation failed! Status: " + response.getStatusCode());

        // ✅ Extract user_id and firstName
        userId = response.jsonPath().getString("user_id");
        firstName = response.jsonPath().getString("user_first_name");

        Assert.assertNotNull(userId, "user_id is null!");
        Assert.assertNotNull(firstName, "firstName is null!");

        System.out.println("User Created Successfully - ID: " + userId + ", Name: " + firstName);
        System.out.println("Response Time: " + (endTime - startTime) + " ms");
    }

    // retrieving the user
    @Test(priority = 2, dependsOnMethods = "testCreateUser")
    public void testGetUser() {
        Assert.assertNotNull(firstName, "First Name is null before GET operation!");

        System.out.println("Fetching User Details for: " + firstName);
        long startTime = System.currentTimeMillis();
        Response response = apiChainingPage.getUser(firstName);
        long endTime = System.currentTimeMillis();

        Assert.assertEquals(response.getStatusCode(), 200, "User retrieval failed! Status: " + response.getStatusCode());

        System.out.println("User Retrieved: ID=" + response.jsonPath().getString("user_id") + ", Name=" + response.jsonPath().getString("user_first_name"));
        System.out.println("Response Time: " + (endTime - startTime) + " ms");
    }

 // updating the user
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
                expectedZipCode = existingZipCode + "1"; // Append '1' for uniqueness
                userAddress.put("zipCode", expectedZipCode);
            } else {
                throw new RuntimeException("❌ zipCode key is missing in userAddress!");
            }

            updateData.put("userAddress", userAddress); 
        } else {
            throw new RuntimeException("userAddress is missing or not in the correct format!");
        }

        
        System.out.println("Final PUT Request Data: " + updateData);

        System.out.println("Updating User ID: " + userId + " with Data: " + updateData);
        long startTime = System.currentTimeMillis();
        Response response = apiChainingPage.updateUser(userId, updateData);
        long endTime = System.currentTimeMillis();

        
        Assert.assertEquals(response.getStatusCode(), 200, "User update failed! Status: " + response.getStatusCode());

       
        String responseLastName = response.jsonPath().getString("user_last_name");
        String responseZipCode = response.jsonPath().getString("userAddress.zipCode");

        if (responseZipCode == null) {
            throw new RuntimeException("Zip Code not found in API response! Full Response: " + response.asString());
        }

        Assert.assertEquals(responseLastName, updatedLastName, "Last Name update mismatch!");
        Assert.assertEquals(responseZipCode, expectedZipCode, "Zip Code update mismatch!");

        System.out.println("User Updated Successfully: Last Name=" + responseLastName + ", ZipCode=" + responseZipCode);
        System.out.println("Response Time: " + (endTime - startTime) + " ms");
    }
           

    // Deleting the user that we created
    @Test(priority = 4, dependsOnMethods = "testUpdateUser")
    public void testDeleteUser() {
        Assert.assertNotNull(firstName, "First Name is null before DELETE operation!");

        System.out.println("Deleting User: " + firstName);
        long startTime = System.currentTimeMillis();
        Response response = apiChainingPage.deleteUser(firstName);
        long endTime = System.currentTimeMillis();
     
        Assert.assertEquals(response.getStatusCode(), 200, "User deletion failed! Status: " + response.getStatusCode());

        System.out.println("User Deleted Successfully!");
        System.out.println("Response Time: " + (endTime - startTime) + " ms");
    }
}