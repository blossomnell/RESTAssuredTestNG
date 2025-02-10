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

    private static final String NON_CHAINING_JSON = ConfigReader.getNonChainingJsonPath();
    private static List<JSONObject> testDataList = new ArrayList<>(); 

    
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
            	System.out.println("Test Case: " + jsonObject.get("test_case"));

            }

        } catch (IOException | ParseException e) {
            throw new RuntimeException("Error reading JSON file: " + NON_CHAINING_JSON, e);
        }
    }


    private static Object[][] filterTestCases(String prefix) {
        List<JSONObject> filteredCases = new ArrayList<>();

        for (JSONObject data : testDataList) {
            String testCase = (String) data.get("test_case");
            if (testCase.startsWith(prefix)) {
                filteredCases.add(data);
            }
        }

        Object[][] testDataArray = new Object[filteredCases.size()][1];
        for (int i = 0; i < filteredCases.size(); i++) {
            testDataArray[i][0] = filteredCases.get(i);
        }

        return testDataArray;
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
