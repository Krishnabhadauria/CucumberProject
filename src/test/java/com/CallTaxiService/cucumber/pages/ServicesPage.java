package com.CallTaxiService.cucumber.pages;

import org.openqa.selenium.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServicesPage {
	
	private static final Logger log = LogManager.getLogger(ServicesPage.class);
	WebDriver driver;

	// POM locators
	private final By miniCabLink = By.cssSelector("a[href='mini.html']");
	private final By microCabLink = By.cssSelector("a[href='micro.html']");
	private final By sedanCabLink = By.cssSelector("a[href='sedan.html']");
	private final By suvCabLink = By.cssSelector("a[href='suv.html']");

	public ServicesPage(WebDriver driver) {
	    this.driver = driver;
	}

	public void clickCabLink(String cabType) {
	    cabType = cabType.toLowerCase().trim();
	    log.info("Attempting to click on cab type link: " + cabType);

	    switch (cabType) {
	        case "mini":
	            driver.findElement(miniCabLink).click();
	            log.info("Clicked on Mini cab link");
	            break;
	        case "micro":
	            driver.findElement(microCabLink).click();
	            log.info("Clicked on Micro cab link");
	            break;
	        case "sedan":
	            driver.findElement(sedanCabLink).click();
	            log.info("Clicked on Sedan cab link");
	            break;
	        case "suv":
	            driver.findElement(suvCabLink).click();
	            log.info("Clicked on Suv cab link");
	            break;
	        default:
	            log.error("Invalid cab type received: " + cabType);
	            throw new IllegalArgumentException("Invalid cab type: " + cabType);
	    }
	}

	public String getCurrentURL() {
	    String url = driver.getCurrentUrl();
	    log.info("Current page URL: " + url);
	    return url;
	}



}