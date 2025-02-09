package baseTest;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import Utilities.ConfigReader;
import Utilities.LoggerLoad; 

public class BaseTest {

    public static final RequestSpecification requestSpec;

    static {
        try {
            if (!ConfigReader.isConfigLoaded()) {
                throw new RuntimeException("Config file not loaded properly! Exiting tests.");
            }

            RestAssured.baseURI = ConfigReader.getBaseUrl();

            requestSpec = RestAssured.given()
                    .auth().preemptive().basic(ConfigReader.getUsername(), ConfigReader.getPassword())
                    .header("Content-Type", "application/json"); 

            LoggerLoad.info("RestAssured Request Specification Initialized Successfully");

        } catch (Exception e) {
            LoggerLoad.error("Failed to initialize RequestSpecification: " + e.getMessage());
            throw new RuntimeException("Failed to initialize RequestSpecification", e);
        }
    }
}
