package com.experitest.accessibility;

import com.experitest.client.Client;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SeeDriver implements Driver{
    Client client;
    public SeeDriver(Object objectDriver){
        this.client = (Client)objectDriver;
    }
    @Override
    public void accessibilityStart() {
        client.accessibilityStart();
    }

    @Override
    public void accessibilityStop() {
        client.accessibilityStop();
    }

    @Override
    public String getPageSource() {
        return client.getVisualDump("NATIVE");
    }

    @Override
    public void launch(String activity) {
        client.launch(activity, false, false);
    }

    @Override
    public String accessibilityMoveNext() {
        return client.accessibilityMoveNext();
    }

    @Override
    public BufferedImage getScreenshot() throws IOException {
        String path = client.capture();
        return ImageIO.read(new File(path));
    }
}
