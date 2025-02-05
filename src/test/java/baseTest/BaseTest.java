package baseTest;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import Utilities.ConfigReader;

public class BaseTest {

    public static RequestSpecification requestSpec;

 
    static {
        try {
            if (!ConfigReader.isConfigLoaded()) {
                throw new RuntimeException("❌ Config file not loaded properly! Exiting tests.");
            }

            RestAssured.baseURI = ConfigReader.getBaseUrl();

            requestSpec = RestAssured.given()
                    .auth().preemptive().basic(ConfigReader.getUsername(), ConfigReader.getPassword());

            System.out.println("✅ RestAssured Request Specification Initialized Successfully");

        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to initialize RequestSpecification: " + e.getMessage());
        }
    }
}
