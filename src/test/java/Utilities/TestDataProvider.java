package Utilities;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.DataProvider;

public class TestDataProvider {

    static String NON_CHAINING_JSON = ConfigReader.getNonChainingJsonPath();

    // Reads JSON file once and returns all test cases as a list
    private static List<JSONObject> readJsonArray(String filePath) {
        JSONParser parser = new JSONParser();
        List<JSONObject> testDataList = new ArrayList<>();

        try (FileReader reader = new FileReader(filePath)) {
            JSONArray jsonArray = (JSONArray) parser.parse(reader);
            for (Object obj : jsonArray) {
                testDataList.add((JSONObject) obj);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("Error reading JSON file: " + filePath);
        }
        return testDataList;
    }

    // Fetch specific test data by test_case name
    private static Object[][] getTestData(String filePath, String testCase) {
        List<JSONObject> jsonData = readJsonArray(filePath);
        for (JSONObject jsonObject : jsonData) {
            if (jsonObject.containsKey("test_case") && jsonObject.get("test_case").equals(testCase)) {
                return new Object[][] {{ jsonObject }};
            }
        }
        System.err.println("Warning: Test case '" + testCase + "' not found in JSON.");
        return new Object[0][0]; // Return empty object if no match found
    }

    // Data Provider for Non-Chaining Tests (Independent Tests)
    @DataProvider(name = "NonChainingData")
    public Object[][] getNonChainingData() {
        return new Object[][] {
            getTestData(NON_CHAINING_JSON, "Create User - Positive"),
            getTestData(NON_CHAINING_JSON, "Create User - Negative (Special Characters in First Name)"),
            getTestData(NON_CHAINING_JSON, "Get User - Positive"),
            getTestData(NON_CHAINING_JSON, "Get User - Negative (Wrong Endpoint)"),
            getTestData(NON_CHAINING_JSON, "Update User - Positive"),
            getTestData(NON_CHAINING_JSON, "Update User - Negative (Special Characters in First Name)"),
            getTestData(NON_CHAINING_JSON, "Delete User - Positive"),
            getTestData(NON_CHAINING_JSON, "Delete User - Negative (Invalid User ID)")
        };
    }
}
