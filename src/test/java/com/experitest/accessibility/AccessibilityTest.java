package com.experitest.accessibility;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

public class AccessibilityTest {
    private String reportDirectory = "reports";
    private String reportFormat = "xml";
    private String testName = "Untitled";
    protected IOSDriver<IOSElement> driver = null;

    DesiredCapabilities dc = new DesiredCapabilities();
    private String accessKey = "eyJ4cC51IjozNDY2MjMsInhwLnAiOjIsInhwLm0iOiJNVFUzTkRBNE5qRTJOVEEwTmciLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE4ODk0NDYxNjUsImlzcyI6ImNvbS5leHBlcml0ZXN0In0.BBtPr5V0WxWyySPrVtrwIgGaSoE9ewV6NRplFyKG3fU";

    @BeforeMethod
    public void setUp() throws MalformedURLException {

        dc.setCapability("testName", "Quick Start iOS Native Demo");
        dc.setCapability("accessKey", accessKey);
        dc.setCapability("deviceQuery", "@os='ios' and @category='PHONE'");
        dc.setCapability(MobileCapabilityType.APP, "cloud:com.experitest.ExperiBank");
        dc.setCapability(IOSMobileCapabilityType.BUNDLE_ID, "com.experitest.ExperiBank");
        driver = new IOSDriver<>(new URL("https://demo.experitest.com/wd/hub"), dc);    }



    @Test
    public void testUntitled() throws Exception{
        AccessibilityUtils.validate(driver, "Login_page", new File("results\\login_page"),
                Issue.Type.SIZE_TOO_SMALL_HEIGHT,
                Issue.Type.SIZE_TOO_SMALL_WIDTH,
                Issue.Type.CONTRAST,
                Issue.Type.NO_ACCESSIBILITY_INFO,
                Issue.Type.IMPORTANT_NO_ACCESSIBILITY,
                Issue.Type.EXPECTED_CONTENT);

    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }

}
