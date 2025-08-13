package com.CallTaxiService.cucumber.hooks;

import com.CallTaxiService.cucumber.base.DriverFactory;
import com.CallTaxiService.cucumber.utils.Reports;
import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import io.cucumber.java.*;


public class Hooks {

    private static final String reportPath = "target/ExtentReport.html";
    public static ExtentReports extent;
    public static ExtentTest test;

    @BeforeAll
    public static void initReport() {
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setDocumentTitle("CallTaxi Report");
        sparkReporter.config().setReportName("CallTaxi Automation Results");

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
    }

    @AfterAll
    public static void flushReport() {
        extent.flush();
    }

    @Before
    public void setUp(Scenario scenario) {
        DriverFactory.initDriver();
        test = extent.createTest(scenario.getName());
    }

    @After
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            String screenshotPath = Reports.captureScreenshot(
                DriverFactory.getDriver(), scenario.getName().replaceAll(" ", "_")
            );
            test.fail("Scenario failed. Screenshot attached.")
                .addScreenCaptureFromPath(screenshotPath);
        } else {
            test.pass("Scenario passed");
        }

        DriverFactory.quitDriver();
    }
}
