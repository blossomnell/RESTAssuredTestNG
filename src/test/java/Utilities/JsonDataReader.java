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

    // ✅ Fetches update data for PUT request (Last Name & Zip Code only)
    public static Map<String, Object> getUpdateTestData() {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("user_last_name", "UpdatedAppoopan");
        updateData.put("zipCode", "99999"); // ✅ Hardcoded update data

        return updateData;
    }
}
