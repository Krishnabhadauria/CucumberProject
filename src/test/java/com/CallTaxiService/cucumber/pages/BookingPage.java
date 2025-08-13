package com.CallTaxiService.cucumber.pages;

import com.CallTaxiService.cucumber.utils.Reports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration; // Ensure this is imported

public class BookingPage {

    private WebDriver driver;
    private WebDriverWait wait;
    private ExtentTest test;
    private final Duration defaultTimeout = Duration.ofSeconds(10); // Store timeout duration

    // Constructor with ExtentTest
    public BookingPage(WebDriver driver, ExtentTest test) {
        this.driver = driver;
        this.test = test;
        this.wait = new WebDriverWait(driver, defaultTimeout); // Use the stored timeout
    }

    // Constructor without ExtentTest
    public BookingPage(WebDriver driver) {
        this(driver, null);
    }

    // Locators (KEEP AS IS from previous response)
    private final By fullName = By.id("fullname");
    private final By phoneNumber = By.id("phonenumber");
    private final By emailField = By.id("email");
    private final By tripLong = By.id("long");
    private final By tripLocal = By.id("local");
    private final By cabSelect = By.id("cabselect");
    private final By cabType = By.id("cabType");
    private final By pickupDate = By.id("pickupdate");
    private final By pickupTime = By.id("pickuptime");
    private final By passengerCount = By.id("passenger");
    private final By tripTypeOneway = By.id("oneway");
    private final By tripTypeRoundtrip = By.id("roundtrip");
    private final By bookNowButton = By.id("submitted");

    // Error messages - Make sure these are accurate IDs from your HTML!
    private final By nameError = By.id("invalidname");
    private final By phoneError = By.id("invalidphno");
    private final By emailFormatOrMissingError = By.id("invalidemail");
    private final By tripError = By.id("invalidtrip");
    private final By cabError = By.id("invalidcab");
    private final By passengerError = By.id("invalidcount");
    private final By confirmationMessage = By.id("confirm");

    // NEW LOCATORS FOR DATE AND TIME ERRORS (KEEP AS IS from previous response)
    private final By dateError = By.id("invaliddate");
    private final By timeError = By.id("invalidtime");
    private final By tripTypeError = By.id("invalidtriptype");

    // ================= Utility Methods (KEEP AS IS from previous response) =================

    private String normalizeOption(String input) {
        return input != null ? input.trim() : "";
    }

    private void clearAndSend(By locator, String value) {
        if (value != null) {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            el.clear();
            if (!value.trim().isEmpty()) {
                el.sendKeys(value.trim());
            }
        }
    }

    private void safeSelect(By locator, String visibleText) {
        if (visibleText != null && !visibleText.trim().isEmpty()) {
            WebElement dropdownElement = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            new Select(dropdownElement).selectByVisibleText(normalizeOption(visibleText));
        }
    }

    // ================== Form Actions (KEEP AS IS from previous response) ==================

    public void enterFullName(String name) {
        clearAndSend(fullName, name);
    }

    public void enterPhone(String phone) {
        clearAndSend(phoneNumber, phone);
    }

    public void enterEmail(String email) {
        clearAndSend(emailField, email);
    }

    public void selectTrip(String type) {
        if ("long".equalsIgnoreCase(type)) {
            wait.until(ExpectedConditions.elementToBeClickable(tripLong)).click();
        } else if ("local".equalsIgnoreCase(type)) {
            wait.until(ExpectedConditions.elementToBeClickable(tripLocal)).click();
        }
    }

    public void selectCab(String cab) {
        safeSelect(cabSelect, formatTitleCase(cab));
    }

    public void selectCabType(String type) {
        safeSelect(cabType, type);
    }

    public void enterPickupDate(String date) {
        clearAndSend(pickupDate, date);
    }

    public void enterPickupTime(String time) {
        clearAndSend(pickupTime, time);
    }

    public void selectPassengerCount(String count) {
        safeSelect(passengerCount, count);
    }

    public void chooseTripType(String tripType) {
        if ("oneway".equalsIgnoreCase(tripType)) {
            wait.until(ExpectedConditions.elementToBeClickable(tripTypeOneway)).click();
        } else if ("roundtrip".equalsIgnoreCase(tripType)) {
            wait.until(ExpectedConditions.elementToBeClickable(tripTypeRoundtrip)).click();
        }
    }

    public void clickBookNow() {
        wait.until(ExpectedConditions.elementToBeClickable(bookNowButton)).click();
        if (test != null) {
            Reports.generateReport(driver, test, Status.INFO, "Clicked Book Now button");
        }
    }

    // ============== Validation & Result Retrieval (Only getErrorText modified) ==============

    public String getDisplayedConfirmationMessage() {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(confirmationMessage));
        String text = element.getText().trim();
        if (test != null) {
            Reports.generateReport(driver, test, Status.INFO, "Confirmation message found: " + text);
        }
        return text;
    }

    /**
     * Generic method to get error text. Will throw NoSuchElementException or TimeoutException if not found.
     * BookingSteps will catch and fail if needed.
     * @param locator The By locator for the error element.
     * @param fieldName A user-friendly name for the field for reporting.
     * @return The text of the error message.
     * @throws NoSuchElementException if the element is not found within the timeout.
     */
    public String getErrorText(By locator, String fieldName) {
        try {
            WebElement errorElement = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            String text = errorElement.getText().trim();
            if (test != null) {
                Reports.generateReport(driver, test, Status.INFO, fieldName + " error message found: " + text);
            }
            return text;
        } catch (TimeoutException e) {
            // This means the error message was NOT displayed when expected.
            // Let the calling step (BookingSteps) handle the failure explicitly.
            if (test != null) {
                Reports.generateReport(driver, test, Status.FAIL, fieldName + " error message NOT displayed within timeout.");
            }
            // Use defaultTimeout directly as getTimeout() is deprecated/removed in newer Selenium versions
            throw new NoSuchElementException("Expected " + fieldName + " error message not found: " + locator + ". Timeout: " + defaultTimeout.getSeconds() + "s");
        }
    }

    public String getNameError() {
        return getErrorText(nameError, "Name");
    }

    public String getPhoneError() {
        return getErrorText(phoneError, "Phone");
    }

    public String getEmailFormatOrMissingError() {
        return getErrorText(emailFormatOrMissingError, "Email Format/Missing");
    }

    public String getTripError() {
        return getErrorText(tripError, "Trip Selection");
    }

    public String getCabError() {
        return getErrorText(cabError, "Cab Selection");
    }

    public String getPassengerError() {
        return getErrorText(passengerError, "Passenger Count");
    }

    public String getDateError() {
        return getErrorText(dateError, "Pickup Date");
    }

    public String getTimeError() {
        return getErrorText(timeError, "Pickup Time");
    }

    public String getTripTypeError() {
        return getErrorText(tripTypeError, "Trip Type Selection");
    }

    // =============== Helpers (KEEP AS IS from previous response) =================

    private String formatTitleCase(String input) {
        if (input == null || input.trim().isEmpty()) return "";
        String lower = input.trim().toLowerCase();
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
}