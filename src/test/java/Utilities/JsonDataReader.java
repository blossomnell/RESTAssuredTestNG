package Utilities;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;
import java.util.*;

public class JsonDataReader {

    // Fetches a single user's test data for API chaining
    public static Map<String, Object> getChainingTestData() {
        String filePath = System.getProperty("user.dir") + "/src/test/resources/Json/ChainingTestData.json";

        try (FileReader reader = new FileReader(filePath)) {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(reader);

            // Convert JSON Object to HashMap
            Map<String, Object> testData = new HashMap<>();
            for (Object key : jsonObject.keySet()) {
                testData.put(key.toString(), jsonObject.get(key));
            }

            return testData;

        } catch (Exception e) {
            throw new RuntimeException("Error reading JSON file: " + filePath, e);
        }
    }

    // Fetches update data for PUT request (Dynamically modifies Last Name & Zip Code)
    public static Map<String, Object> getUpdateTestData() {
        Map<String, Object> userData = getChainingTestData();  

        Map<String, Object> updateData = new HashMap<>();

        // Dynamically update last name
        if (userData.containsKey("user_last_name")) {
            String existingLastName = userData.get("user_last_name").toString();
            updateData.put("user_last_name", existingLastName + "Updated"); 
        } else {
            throw new RuntimeException("user_last_name field is missing in test data!");
        }

        Object addressObject = userData.get("userAddress");

        if (addressObject instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> userAddress = (Map<String, Object>) addressObject;

            if (userAddress.containsKey("zipCode")) {
                String existingZipCode = String.valueOf(userAddress.get("zipCode"));  
                updateData.put("zipCode", existingZipCode + "1");  
            } else {
                throw new RuntimeException("zipCode field is missing in userAddress!");
            }
        } else {
            throw new RuntimeException("userAddress field is missing or not a valid object in test data!");
        }

        return updateData;
    }

    // Fetches test data for Non-Chaining API tests
    public static List<Map<String, Object>> getNonChainingTestData() {
        String filePath = System.getProperty("user.dir") + "/src/test/resources/Json/NonChainingTestData.json";

        try (FileReader reader = new FileReader(filePath)) {
            JSONParser parser = new JSONParser();
            JSONArray testDataArray = (JSONArray) parser.parse(reader);

            List<Map<String, Object>> testDataList = new ArrayList<>();

            for (Object obj : testDataArray) {
                JSONObject jsonObject = (JSONObject) obj;

                Map<String, Object> testData = new HashMap<>();
                for (Object key : jsonObject.keySet()) {
                    testData.put(key.toString(), jsonObject.get(key));
                }

                testDataList.add(testData);
            }

            return testDataList;

        } catch (Exception e) {
            throw new RuntimeException("Error reading JSON file: " + filePath, e);
        }
    }

    // Fetch a specific test case from Non-Chaining JSON based on the test_case name
    public static Map<String, Object> getNonChainingTestCase(String testCaseName) {
        List<Map<String, Object>> testDataList = getNonChainingTestData();

        for (Map<String, Object> testData : testDataList) {
            if (testData.get("test_case").equals(testCaseName)) {
                return testData;
            }
        }
        throw new RuntimeException("Test case '" + testCaseName + "' not found in Non-Chaining Test Data!");
    }
}
