package com.CallTaxiService.cucumber.stepDefinitions;

import com.CallTaxiService.cucumber.pages.ServicesPage;
import com.CallTaxiService.cucumber.base.DriverFactory;
import com.CallTaxiService.cucumber.configs.ConfigReader;

import io.cucumber.java.en.*;

import org.openqa.selenium.WebDriver;
import org.junit.Assert;
public class ServiceSteps {

    WebDriver driver;
    ServicesPage servicePage;
    String originalHandle;

    @Given("User opens the Services page")
    public void open_services_page() {
        driver = DriverFactory.getDriver();

        // Load URL from config.properties
        String serviceUrl = ConfigReader.getProperty("serviceUrl");
        driver.get(serviceUrl);

        servicePage = new ServicesPage(driver);
        originalHandle = driver.getWindowHandle(); // Save handle for switching later
    }

    @When("User clicks on the {string} service link")
    public void user_clicks_service_link(String cabType) {
        if (servicePage == null) {
            servicePage = new ServicesPage(driver);
        }

        servicePage.clickCabLink(cabType);

        // Wait for and switch to new tab if opened
        boolean switched = false;
        for (int i = 0; i < 5; i++) {
            for (String handle : driver.getWindowHandles()) {
                if (!handle.equals(originalHandle)) {
                    driver.switchTo().window(handle);
                    switched = true;
                    break;
                }
            }
            if (switched) break;
            try {
                Thread.sleep(500); // slight wait for the new tab to register
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Then("The user should be navigated to {string}")
    public void verify_navigation(String expectedPage) {
        String actualURL = servicePage.getCurrentURL();
        System.out.println("[INFO] Expected page: " + expectedPage);
        System.out.println("[INFO] Actual URL: " + actualURL);

        Assert.assertTrue("Expected URL to contain: " + expectedPage + " but was: " + actualURL,
                actualURL.contains(expectedPage));
    }
}
