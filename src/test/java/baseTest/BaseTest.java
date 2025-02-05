package baseTest;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;
import Utilities.ConfigReader;

public class BaseTest {

    @BeforeClass
    public void setup() {
        System.out.println("🚀 Initializing BaseTest...");

        if (!ConfigReader.isConfigLoaded()) {
            throw new RuntimeException("❌ Config file not loaded properly! Exiting tests.");
        }

        // ✅ Validate Base URL
        String baseUrl = ConfigReader.getBaseUrl();
        if (baseUrl == null || baseUrl.isEmpty()) {
            throw new RuntimeException("❌ Base URL is missing in config.properties!");
        }
        RestAssured.baseURI = baseUrl;

        // ✅ Validate Authentication Credentials
        String username = ConfigReader.getUsername();
        String password = ConfigReader.getPassword();
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new RuntimeException("❌ Authentication credentials are missing in config.properties!");
        }
        RestAssured.authentication = RestAssured.preemptive().basic(username, password);

        // ✅ Print Setup Details
        System.out.println("============================================");
        System.out.println("🚀 BaseTest Setup Completed");
        ConfigReader.printConfigDetails();
        System.out.println("✅ Base URI Set: " + RestAssured.baseURI);
        System.out.println("✅ Authentication Set for User: " + username);
        System.out.println("============================================");
    }
}
