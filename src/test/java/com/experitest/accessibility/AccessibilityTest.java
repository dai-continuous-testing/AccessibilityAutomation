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
    private String accessKey = "eyJ4cC51IjozNDY2MjMsInhwLnAiOjIsInhwLm0iOiJNVFU0TkRnMk1qRXhPVE15TWciLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE5MDAyMjIxMTksImlzcyI6ImNvbS5leHBlcml0ZXN0In0.TnZJMSP7K-gmWOfQlrbB_QxxQ6qFu2meT-VLlugJSOM";

    @BeforeMethod
    public void setUp() throws MalformedURLException {

        dc.setCapability("testName", "Quick Start iOS Native Demo");
        dc.setCapability("accessKey", accessKey);
        dc.setCapability("deviceQuery", "@os='ios' and @category='PHONE'");
        dc.setCapability(MobileCapabilityType.APP, "cloud:com.experitest.ExperiBank");
        dc.setCapability(IOSMobileCapabilityType.BUNDLE_ID, "com.experitest.ExperiBank");
        //dc.setCapability(IOSMobileCapabilityType.BUNDLE_ID, "com.apple.Preferences");

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
