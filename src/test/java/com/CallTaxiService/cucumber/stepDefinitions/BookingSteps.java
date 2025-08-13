package com.CallTaxiService.cucumber.stepDefinitions;

import com.CallTaxiService.cucumber.pages.BookingPage;
import com.CallTaxiService.cucumber.base.DriverFactory;
import com.CallTaxiService.cucumber.utils.ExcelUtil;
import com.CallTaxiService.cucumber.configs.ConfigReader;
import static com.CallTaxiService.cucumber.hooks.Hooks.test; // Assuming ExtentTest is available via Hooks

import io.cucumber.java.en.*;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.NoSuchElementException; // Import for catching
import org.openqa.selenium.TimeoutException; // Import for catching
import com.CallTaxiService.cucumber.utils.LoggerUtil;
import com.CallTaxiService.cucumber.utils.Reports;
import org.apache.logging.log4j.Logger;

public class BookingSteps {

    WebDriver driver;
    BookingPage bookingPage;
    List<Map<String, String>> data;
    Map<String, String> testRow;
    Logger logger = LoggerUtil.getLogger(BookingSteps.class);

    @Given("User launches the browser and opens the cab booking page")
    public void user_opens_booking_page() {
        driver = DriverFactory.getDriver();
        String url = ConfigReader.getProperty("bookingUrl");
        if (url == null || url.trim().isEmpty()) {
            Assert.fail("Booking URL is not configured or is empty in config.properties. Please set 'bookingUrl'.");
        }
        driver.get(url);
        bookingPage = new BookingPage(driver, test); // Pass ExtentTest instance
        logger.info("Browser launched and booking page opened at: " + url);
    }

    @When("User fills the form with valid details:")
    public void user_fills_form(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> formData = dataTable.asMaps().get(0);
        logger.info("Filling form with: " + formData);
        try {
            bookingPage.enterFullName(formData.get("Name"));
            bookingPage.enterPhone(formData.get("Phone"));
            bookingPage.enterEmail(formData.get("Email"));
            bookingPage.selectTrip(formData.get("Trip"));
            bookingPage.selectCab(formData.get("Cab"));
            bookingPage.selectCabType(formData.get("CabType"));
            bookingPage.enterPickupDate(formData.get("Date"));
            bookingPage.enterPickupTime(formData.get("Time"));
            bookingPage.selectPassengerCount(formData.get("Passenger"));
            bookingPage.chooseTripType(formData.get("TripType"));
            logger.info("Form filled successfully.");
        } catch (Exception e) {
            logger.error("Error while filling form: ", e);
            Reports.captureScreenshot(driver, "FormFillingError");
            Assert.fail("Form filling failed: " + e.getMessage());
        }
    }

    @When("User reads booking data from Excel sheet {string} and row {int}")
    public void user_reads_excel_data(String sheet, Integer rowNum) {
        driver = DriverFactory.getDriver();
        String url = ConfigReader.getProperty("bookingUrl");
        if (url == null || url.trim().isEmpty()) {
            Assert.fail("Booking URL is not configured or is empty in config.properties for Excel scenario.");
        }
        driver.get(url); // This will still open the page every time this step runs
        bookingPage = new BookingPage(driver, test); // Pass ExtentTest instance
        data = ExcelUtil.getData(sheet);
        if (data == null || data.isEmpty()) {
            Assert.fail("Excel sheet '" + sheet + "' is empty or could not be read.");
        }
        if (rowNum >= data.size() || rowNum < 0) {
             Assert.fail("Row number " + rowNum + " is out of bounds for sheet '" + sheet + "' which has " + data.size() + " rows.");
        }
        testRow = data.get(rowNum);
        logger.info("Fetched test data for row {} in sheet {}", rowNum, sheet);
    }

    @When("User fills the form using Excel data")
    public void user_fills_form_excel() {
        if (testRow == null) {
            Assert.fail("Excel test data not loaded. Ensure 'User reads booking data...' step ran successfully.");
        }
        try {
            bookingPage.enterFullName(testRow.get("Name"));
            bookingPage.enterPhone(testRow.get("Phone"));
            bookingPage.enterEmail(testRow.get("Email"));
            bookingPage.selectTrip(testRow.get("Trip"));
            bookingPage.selectCab(testRow.get("Cab"));
            bookingPage.selectCabType(testRow.get("CabType"));
            bookingPage.enterPickupDate(testRow.get("Date"));
            bookingPage.enterPickupTime(testRow.get("Time"));
            bookingPage.selectPassengerCount(testRow.get("Passenger"));
            bookingPage.chooseTripType(testRow.get("TripType"));
            logger.info("Form filled from Excel data successfully.");
        } catch (Exception e) {
            logger.error("Error while filling form from Excel: ", e);
            Reports.captureScreenshot(driver, "ExcelFormFillingError");
            Assert.fail("Excel-based form filling failed: " + e.getMessage());
        }
    }

    @And("User clicks on Book Now button")
    public void user_clicks_book_now() {
        try {
            bookingPage.clickBookNow();
            logger.info("Clicked on Book Now button.");
        } catch (Exception e) {
            logger.error("Failed to click Book Now button: ", e);
            Reports.captureScreenshot(driver, "ClickBookNowError");
            Assert.fail("Book Now action failed: " + e.getMessage());
        }
    }

    // *** IMPORTANT: This is for successful booking confirmation ***
    @Then("Booking confirmation message {string} should be displayed")
    public void booking_confirmation_should_be_displayed(String expected) {
        try {
            String actualMessage = bookingPage.getDisplayedConfirmationMessage();
            Assert.assertTrue("Confirmation message mismatch! Expected: '" + expected + "', Actual: '" + actualMessage + "'",
                              actualMessage.contains(expected));
            logger.info("Booking confirmation message verified: " + actualMessage);
        } catch (NoSuchElementException | TimeoutException e) {
            logger.error("Booking confirmation message NOT displayed. Expected: " + expected, e);
            Reports.captureScreenshot(driver, "ConfirmationMessageNotDisplayed");
            // Fail the test if the confirmation message is not found
            Assert.fail("BUG: Expected booking confirmation message '" + expected + "' was NOT displayed. This indicates a bug. " + e.getMessage());
        } catch (AssertionError e) {
            logger.error("Booking confirmation message mismatch. Expected: " + expected, e);
            Reports.captureScreenshot(driver, "ConfirmationMessageMismatch");
            throw e; // Re-throw assertion errors
        }
    }

    // *** MODIFIED: Handles invalid email format and general email errors ***
    // Ensure this matches the feature file step: "Then Error message {string} should be shown under Email field"
    @Then("Email error message {string} should be displayed") // This step name is slightly different than the one below, keeping both for now
    public void email_error_should_be_displayed(String expected) {
        try {
            String actualMessage = bookingPage.getEmailFormatOrMissingError(); // Using the specific getter
            Assert.assertTrue("Email error message mismatch! Expected: '" + expected + "', Actual: '" + actualMessage + "'",
                              actualMessage.contains(expected));
            logger.info("Email field error validated: " + actualMessage);
        } catch (NoSuchElementException | TimeoutException e) {
            logger.error("Email error message NOT displayed. Expected: " + expected, e);
            Reports.captureScreenshot(driver, "EmailErrorNotDisplayed");
            // Fail the test if the error message is not found
            Assert.fail("BUG: Expected email error message '" + expected + "' was NOT displayed. This indicates a bug. " + e.getMessage());
        } catch (AssertionError e) {
            logger.error("Email error message mismatch. Expected: " + expected, e);
            Reports.captureScreenshot(driver, "EmailErrorMessageMismatch");
            throw e; // Re-throw assertion errors
        }
    }

    // This step definition matches the 'Then Error message "{string}" should be shown under Email field' from the feature file
    @Then("Error message {string} should be shown under Email field")
    public void email_field_error_displayed(String expected) { // Renamed to avoid ambiguity with above
        try {
            String actualMessage = bookingPage.getEmailFormatOrMissingError();
            Assert.assertTrue("Email field error mismatch! Expected: '" + expected + "', Actual: '" + actualMessage + "'",
                              actualMessage.contains(expected));
            logger.info("Email field error validated: " + actualMessage);
        } catch (NoSuchElementException | TimeoutException e) {
            logger.error("Email field error message NOT displayed. Expected: " + expected, e);
            Reports.captureScreenshot(driver, "EmailFieldErrorNotDisplayed");
            Assert.fail("BUG: Expected email field error message '" + expected + "' was NOT displayed. This indicates a bug. " + e.getMessage());
        } catch (AssertionError e) {
            logger.error("Email field error mismatch. Expected: " + expected, e);
            Reports.captureScreenshot(driver, "EmailFieldErrorMismatch");
            throw e;
        }
    }


    @Then("Error message {string} should be shown under Name field")
    public void name_error_displayed(String expected) {
        try {
            String actualMessage = bookingPage.getNameError();
            Assert.assertTrue("Name error message mismatch! Expected: '" + expected + "', Actual: '" + actualMessage + "'",
                              actualMessage.contains(expected));
            logger.info("Name field error validated: " + actualMessage);
        } catch (NoSuchElementException | TimeoutException e) {
            logger.error("Name error message NOT displayed. Expected: " + expected, e);
            Reports.captureScreenshot(driver, "NameErrorNotDisplayed");
            Assert.fail("BUG: Expected name error message '" + expected + "' was NOT displayed. This indicates a bug. " + e.getMessage());
        } catch (AssertionError e) {
            logger.error("Name field error mismatch. Expected: " + expected, e);
            Reports.captureScreenshot(driver, "NameErrorMessageMismatch");
            throw e;
        }
    }

    @Then("Error message {string} should be shown under Trip selection")
    public void trip_error_displayed(String expected) {
        try {
            String actualMessage = bookingPage.getTripError();
            Assert.assertTrue("Trip selection error message mismatch! Expected: '" + expected + "', Actual: '" + actualMessage + "'",
                              actualMessage.contains(expected));
            logger.info("Trip selection error validated: " + actualMessage);
        } catch (NoSuchElementException | TimeoutException e) {
            logger.error("Trip error message NOT displayed. Expected: " + expected, e);
            Reports.captureScreenshot(driver, "TripErrorNotDisplayed");
            Assert.fail("BUG: Expected trip error message '" + expected + "' was NOT displayed. This indicates a bug. " + e.getMessage());
        } catch (AssertionError e) {
            logger.error("Trip selection error mismatch. Expected: " + expected, e);
            Reports.captureScreenshot(driver, "TripErrorMessageMismatch");
            throw e;
        }
    }

    @Then("Error message {string} should be shown under Passenger count")
    public void passenger_error_displayed(String expected) {
        try {
            String actualMessage = bookingPage.getPassengerError();
            Assert.assertTrue("Passenger count error message mismatch! Expected: '" + expected + "', Actual: '" + actualMessage + "'",
                              actualMessage.contains(expected));
            logger.info("Passenger count error validated: " + actualMessage);
        } catch (NoSuchElementException | TimeoutException e) {
            logger.error("Passenger error message NOT displayed. Expected: " + expected, e);
            Reports.captureScreenshot(driver, "PassengerErrorNotDisplayed");
            Assert.fail("BUG: Expected passenger error message '" + expected + "' was NOT displayed. This indicates a bug. " + e.getMessage());
        } catch (AssertionError e) {
            logger.error("Passenger error mismatch. Expected: " + expected, e);
            Reports.captureScreenshot(driver, "PassengerErrorMessageMismatch");
            throw e;
        }
    }

    // --- NEW STEP DEFINITIONS FOR PHONE, DATE, TIME, TRIP TYPE VALIDATION ---

    @Then("Error message {string} should be shown under Phone field")
    public void phone_error_displayed(String expected) {
        try {
            String actualMessage = bookingPage.getPhoneError();
            Assert.assertTrue("Phone error message mismatch! Expected: '" + expected + "', Actual: '" + actualMessage + "'",
                              actualMessage.contains(expected));
            logger.info("Phone field error validated: " + actualMessage);
        } catch (NoSuchElementException | TimeoutException e) {
            logger.error("Phone error message NOT displayed. Expected: " + expected, e);
            Reports.captureScreenshot(driver, "PhoneErrorNotDisplayed");
            Assert.fail("BUG: Expected phone error message '" + expected + "' was NOT displayed. This indicates a bug in the application. " + e.getMessage());
        } catch (AssertionError e) {
            logger.error("Phone field error mismatch. Expected: " + expected, e);
            Reports.captureScreenshot(driver, "PhoneErrorMessageMismatch");
            throw e;
        }
    }

    @Then("Error message {string} should be shown under Pickup Date field")
    public void pickup_date_error_displayed(String expected) {
        try {
            String actualMessage = bookingPage.getDateError();
            Assert.assertTrue("Pickup Date error message mismatch! Expected: '" + expected + "', Actual: '" + actualMessage + "'",
                              actualMessage.contains(expected));
            logger.info("Pickup Date field error validated: " + actualMessage);
        } catch (NoSuchElementException | TimeoutException e) {
            logger.error("Pickup Date error message NOT displayed. Expected: " + expected, e);
            Reports.captureScreenshot(driver, "PickupDateErrorNotDisplayed");
            Assert.fail("BUG: Expected Pickup Date error message '" + expected + "' was NOT displayed. This indicates a bug in the application. " + e.getMessage());
        } catch (AssertionError e) {
            logger.error("Pickup Date error mismatch. Expected: " + expected, e);
            Reports.captureScreenshot(driver, "PickupDateErrorMessageMismatch");
            throw e;
        }
    }

    @Then("Error message {string} should be shown under Pickup Time field")
    public void pickup_time_error_displayed(String expected) {
        try {
            String actualMessage = bookingPage.getTimeError();
            Assert.assertTrue("Pickup Time error message mismatch! Expected: '" + expected + "', Actual: '" + actualMessage + "'",
                              actualMessage.contains(expected));
            logger.info("Pickup Time field error validated: " + actualMessage);
        } catch (NoSuchElementException | TimeoutException e) {
            logger.error("Pickup Time error message NOT displayed. Expected: " + expected, e);
            Reports.captureScreenshot(driver, "PickupTimeErrorNotDisplayed");
            Assert.fail("BUG: Expected Pickup Time error message '" + expected + "' was NOT displayed. This indicates a bug in the application. " + e.getMessage());
        } catch (AssertionError e) {
            logger.error("Pickup Time error mismatch. Expected: " + expected, e);
            Reports.captureScreenshot(driver, "PickupTimeErrorMessageMismatch");
            throw e;
        }
    }

    @Then("Error message {string} should be shown under Trip Type field")
    public void trip_type_error_displayed(String expected) {
        try {
            String actualMessage = bookingPage.getTripTypeError();
            Assert.assertTrue("Trip Type error message mismatch! Expected: '" + expected + "', Actual: '" + actualMessage + "'",
                              actualMessage.contains(expected));
            logger.info("Trip Type field error validated: " + actualMessage);
        } catch (NoSuchElementException | TimeoutException e) {
            logger.error("Trip Type error message NOT displayed. Expected: " + expected, e);
            Reports.captureScreenshot(driver, "TripTypeErrorNotDisplayed");
            Assert.fail("BUG: Expected Trip Type error message '" + expected + "' was NOT displayed. This indicates a bug in the application. " + e.getMessage());
        } catch (AssertionError e) {
            logger.error("Trip Type error mismatch. Expected: " + expected, e);
            Reports.captureScreenshot(driver, "TripTypeErrorMessageMismatch");
            throw e;
        }
    }
}