package com.experitest.accessibility;

import com.experitest.client.Client;
import com.experitest.client.GridClient;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;

public class AccessibilitySeeTest {
    private String host = "localhost";
    private int port = 8889;
    private String accessKey = "eyJ4cC51IjozNDY2MjMsInhwLnAiOjIsInhwLm0iOiJNVFUzTkRBNE5qRTJOVEEwTmciLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE4ODk0NDYxNjUsImlzcyI6ImNvbS5leHBlcml0ZXN0In0.BBtPr5V0WxWyySPrVtrwIgGaSoE9ewV6NRplFyKG3fU";
    private String projectBaseDirectory = "C:\\Users\\guyar\\workspace\\project2";
    protected Client client = null;
    protected GridClient grid = null;

    @BeforeMethod
    public void beforeEach(){
        // In case your user is assign to a single project you can provide an empty string,
        // otherwise please specify the project name
        grid = new GridClient(accessKey, "demo.experitest.com",443, true);
        client = grid.lockDeviceForExecution("twitter", "@os='ios'", 10, 50000);
        client.setProjectBaseDirectory(projectBaseDirectory);
        client.setReporter("xml", "reports", "twitter");
    }

    @Test
    public void testtwitter() throws Exception{
        // This command "setDevice" is not applicable for GRID execution
        if(client.install("cloud:com.experitest.ExperiBank/.LoginActivity", false, false)){
            // If statement
        }
        client.launch("com.experitest.ExperiBank/.LoginActivity", false, false);

        AccessibilityUtils.validate(client, "Login_page", new File("results\\login_page"),
                Issue.Type.SIZE_TOO_SMALL_HEIGHT,
                Issue.Type.SIZE_TOO_SMALL_WIDTH,
                Issue.Type.CONTRAST,
                Issue.Type.NO_ACCESSIBILITY_INFO,
                Issue.Type.IMPORTANT_NO_ACCESSIBILITY,
                Issue.Type.EXPECTED_CONTENT);

    }

    @AfterMethod
    public void afterEach(){
        // Generates a report of the test case.
        // For more information - https://docs.experitest.com/display/public/SA/Report+Of+Executed+Test
        client.generateReport(false);
        // Releases the client so that other clients can approach the agent in the near future.
        client.releaseClient();
    }

}
