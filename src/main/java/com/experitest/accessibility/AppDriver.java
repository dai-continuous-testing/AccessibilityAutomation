package com.experitest.accessibility;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class AppDriver implements Driver{
    private RemoteWebDriver driver;
    public AppDriver(RemoteWebDriver driver){
        this.driver = driver;
    }

    @Override
    public void accessibilityStart() {
        driver.executeScript("seetest:client.accessibilityStart()");
    }


    @Override
    public void accessibilityStop() {
        driver.executeScript("seetest:client.accessibilityStop()");
    }

    @Override
    public String getPageSource() {
        return driver.getPageSource();
    }

    @Override
    public void launch(String appBundleId) {
        driver.executeScript("seetest:client.launch(\"" + appBundleId + "\", false, false)");
    }

    @Override
    public String accessibilityMoveNext() {
        return (String)driver.executeScript("seetest:client.accessibilityMoveNext()");
    }

    @Override
    public BufferedImage getScreenshot() throws IOException {
        ByteArrayInputStream imgbytes = new ByteArrayInputStream(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
        assert imgbytes.available() > 0;
        return ImageIO.read(imgbytes);
    }

    @Override
    public String getCurrentApplicationName() {
        return (String)driver.executeScript("seetest:client.getCurrentApplicationName()");
    }

}
