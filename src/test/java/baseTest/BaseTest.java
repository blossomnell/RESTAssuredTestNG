package baseTest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import Utilities.ConfigReader;

public class BaseTest {

    public static String crumbToken = ""; // ✅ Store CSRF Token

    @BeforeClass
    public void setup() {
        if (!ConfigReader.isConfigLoaded()) {
            throw new RuntimeException("❌ Config file not loaded properly! Exiting tests.");
        }

        // ✅ Set Base URL
        RestAssured.baseURI = ConfigReader.getBaseUrl();

        // ✅ Set Authentication
        RestAssured.authentication = RestAssured.preemptive().basic(
                ConfigReader.getUsername(),
                ConfigReader.getPassword()
        );

        // ✅ Fetch CSRF Token (Crumb)
        Response response = RestAssured.given().get("/crumbIssuer/api/json");
        if (response.getStatusCode() == 200) {
            crumbToken = response.jsonPath().getString("crumb");
            System.out.println("✅ CSRF Token Retrieved: " + crumbToken);
        } else {
            System.out.println("⚠ Failed to retrieve CSRF Token. Status: " + response.getStatusCode());
        }

        // ✅ Print Setup Details
        System.out.println("============================================");
        System.out.println("🚀 BaseTest Setup Completed");
        ConfigReader.printConfigDetails();
        System.out.println("✅ Base URI Set: " + RestAssured.baseURI);
        System.out.println("✅ Authentication Set for User: " + ConfigReader.getUsername());
        System.out.println("============================================");
    }
}
