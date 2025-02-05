package Utilities;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class JsonDataReader {

    // ✅ Fetches a single user's test data for API chaining
    public static Map<String, Object> getChainingTestData() {
        String filePath = System.getProperty("user.dir") + "/src/test/resources/Json/ChainingTestData.json";

        try (FileReader reader = new FileReader(filePath)) {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(reader);

            // ✅ Convert JSON Object to HashMap
            Map<String, Object> testData = new HashMap<>();
            for (Object key : jsonObject.keySet()) {
                testData.put(key.toString(), jsonObject.get(key));
            }

            return testData;

        } catch (Exception e) {
            throw new RuntimeException("❌ Error reading JSON file: " + filePath, e);
        }
    }

    // ✅ Fetches update data for PUT request (Dynamically modifies Last Name & Zip Code)
    public static Map<String, Object> getUpdateTestData() {
        Map<String, Object> userData = getChainingTestData();  // Fetch existing user data

        Map<String, Object> updateData = new HashMap<>();

        // ✅ Dynamically update last name
        if (userData.containsKey("user_last_name")) {
            String existingLastName = userData.get("user_last_name").toString();
            updateData.put("user_last_name", existingLastName + "Updated"); 
        } else {
            throw new RuntimeException("❌ user_last_name field is missing in test data!");
        }

        // ✅ Dynamically update zipCode with type safety
        Object addressObject = userData.get("userAddress");

        if (addressObject instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> userAddress = (Map<String, Object>) addressObject;

            if (userAddress.containsKey("zipCode")) {
                String existingZipCode = String.valueOf(userAddress.get("zipCode"));  // Convert to string
                updateData.put("zipCode", existingZipCode + "1");  // Append "1" for uniqueness
            } else {
                throw new RuntimeException("❌ zipCode field is missing in userAddress!");
            }
        } else {
            throw new RuntimeException("❌ userAddress field is missing or not a valid object in test data!");
        }

        return updateData;
    }
}
