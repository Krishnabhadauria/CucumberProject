package com.CallTaxiService.cucumber.runners;


import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = ".\\src\\test\\java\\com\\calltaxi\\features",
    glue = {"com.CallTaxiService.cucumber.stepDefinitions", "com.CallTaxiService.cucumber.hooks"},
    plugin = {"pretty", 
              "html:target/CucumberReport.html", 
              "json:target/cucumber.json"},
    //tags="@exceldata",
    monochrome = true
)
public class TestRunner {
}

//implemented tag based runner
/*
tags = @exceldata 
 	for exceldata
tags = @success
	for valid data
tags = @errordetected
	for missing name, phone number, trip type, and no. of passengers
tags = @knownbug
	for the invalid email format bug scenario - 3

*/