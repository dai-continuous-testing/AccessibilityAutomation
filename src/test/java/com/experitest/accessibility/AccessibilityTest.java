package com.experitest.accessibility;

import com.google.gson.Gson;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.remote.MobileCapabilityType;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Level;

public class AccessibilityTest {
    private String reportDirectory = "reports";
    private String reportFormat = "xml";
    private String testName = "Untitled";
    protected IOSDriver<IOSElement> driver = null;

    DesiredCapabilities dc = new DesiredCapabilities();

    @BeforeMethod
    public void setUp() throws MalformedURLException {

        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.SEVERE);

        dc.setCapability("reportDirectory", reportDirectory);
        dc.setCapability("reportFormat", reportFormat);
        dc.setCapability("testName", testName);
        dc.setCapability(MobileCapabilityType.UDID, "80b6fb8ae5b8e447e31cd14b18abac267a591cee");
        driver = new IOSDriver<>(new URL("http://localhost:4723/wd/hub"), dc);
        driver.setLogLevel(Level.INFO);
    }

    @Test
    public void testUntitled() throws Exception{
        Page page = AccessibilityUtils.getPageAccessibilityInformation(driver, "com.apple.mobiletimer", 70, true);
        page.validate(Issue.Type.SIZE_TO_SMALL_HEIGHT, Issue.Type.SIZE_TO_SMALL_WIDTH, Issue.Type.CONTRAST, Issue.Type.NO_ACCESSIBILITY_INFO, Issue.Type.IMPORTANT_NO_ACCESSIBILITY);
        HtmlReportGenerator.generateReport(page, "Settings", new File("results"));
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }

}
