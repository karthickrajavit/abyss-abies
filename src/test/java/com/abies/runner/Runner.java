package com.abies.runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.abies.stepDefinitions"},
        plugin = {"pretty", "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"},
        tags = "@firstVeng")
public class Runner {
}
