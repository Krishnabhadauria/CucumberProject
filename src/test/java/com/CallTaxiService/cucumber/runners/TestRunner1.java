package com.CallTaxiService.cucumber.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

//@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/java/com/CallTaxiService/cucumber/features",
    glue = {"com.CallTaxiService.cucumber.stepDefinitions", "com.CallTaxiService.cucumber.hooks"},
    plugin = {"pretty", 
              "html:target/CucumberReport.html", 
              "json:target/cucumber.json"},
    tags="@exceldata or @success or @errordetected or @knownbug or @nav",
    monochrome = true
)
public class TestRunner1 extends AbstractTestNGCucumberTests{

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
tags = @nav
	navigation.feature file

*/
