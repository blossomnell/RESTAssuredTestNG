package Tests;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pageObjects.GETPage;
import Utilities.TestDataProvider;
import baseTest.BaseTest;

public class GETTest extends BaseTest {
    private GETPage getPage;

    @BeforeClass
    public void setupPage() {
        getPage = new GETPage();
    }

    @Test(dataProvider = "NonChainingData", dataProviderClass = TestDataProvider.class)
    public void testGetUser(String testCase, String firstName) {
        Response response = getPage.getUserByFirstName(firstName);

        if (testCase.contains("Positive")) {
            Assert.assertEquals(response.getStatusCode(), 200, "User should be retrieved successfully!");
        } else if (testCase.contains("Negative")) {
            Assert.assertNotEquals(response.getStatusCode(), 200, "User retrieval should fail!");
        }
    }
}
