package com.CallTaxiService.cucumber.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;

public class Reports {
	
	public static void generateReport(WebDriver driver, ExtentTest test, Status status, String stepMessage) {
	    if (status.equals(Status.PASS)) {
	        test.log(status, stepMessage);
	    } else if (status.equals(Status.FAIL)) {
	        String screenshotPath = captureScreenshot(driver, stepMessage);
	        try {
	            test.log(status, stepMessage, MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
	        } catch (Exception e) {
	            test.log(status, stepMessage + " (screenshot capture failed)");
	            e.printStackTrace();
	        }
	    }
	}

	public static String captureScreenshot(WebDriver driver, String stepName) {
	    String userDir = System.getProperty("user.dir");
	    Date date = new Date();
	    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH_mm_ss");
	    String timestamp = sdf.format(date);
	    String fileName = userDir + File.separator + "screenshots" + File.separator + stepName.replaceAll("[^a-zA-Z0-9]", "_") + "_" + timestamp + ".png";

	    TakesScreenshot scrShot = (TakesScreenshot) driver;
	    File srcFile = scrShot.getScreenshotAs(OutputType.FILE);
	    File destFile = new File(fileName);

	    try {
	        FileUtils.copyFile(srcFile, destFile);
	    } catch (IOException e) {
	        System.out.println("Failed to copy screenshot: " + e.getMessage());
	    }

	    return fileName;
	}

}