package Utilities;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.DataProvider;

public class TestDataProvider {

    private static final String NON_CHAINING_JSON = ConfigReader.getNonChainingJsonPath();
    private static List<JSONObject> testDataList = new ArrayList<>(); // Store test data in memory

    // Reads JSON once and stores data in a list
    static {
        if (NON_CHAINING_JSON == null || NON_CHAINING_JSON.isEmpty()) {
            throw new RuntimeException("Error: JSON file path is null or empty in ConfigReader.");
        }

        try (FileReader reader = new FileReader(NON_CHAINING_JSON)) {
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(reader);
            for (Object obj : jsonArray) {
                testDataList.add((JSONObject) obj);
            }

           
            System.out.println("Loaded Test Cases from JSON:");
            for (JSONObject jsonObject : testDataList) {
                System.out.println("   ➤ " + jsonObject.get("test_case"));
            }

        } catch (IOException | ParseException e) {
            throw new RuntimeException("Error reading JSON file: " + NON_CHAINING_JSON, e);
        }
    }

   
    private static Object[][] filterTestCases(String prefix) {
        List<JSONObject> filteredCases = testDataList.stream()
            .filter(data -> ((String) data.get("test_case")).startsWith(prefix))
            .collect(Collectors.toList()); // Java 8+ compatibility

        return filteredCases.stream().map(data -> new Object[]{data}).toArray(Object[][]::new);
    }

    
    @DataProvider(name = "PostData")
    public Object[][] getPostData() {
        return filterTestCases("Create User");
    }

    
    @DataProvider(name = "GetData")
    public Object[][] getGetData() {
        return filterTestCases("Get User");
    }

    
    @DataProvider(name = "PutData")
    public Object[][] getPutData() {
        return filterTestCases("Update User");
    }

   
    @DataProvider(name = "DeleteData")
    public Object[][] getDeleteData() {
        return filterTestCases("Delete User");
    }
}
