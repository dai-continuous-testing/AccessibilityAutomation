package com.experitest.accessibility;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface Driver {
    void accessibilityStart();
    void accessibilityStop();
    String getPageSource();
    void launch(String activity);
    String accessibilityMoveNext();
    BufferedImage getScreenshot() throws IOException;
    String getCurrentApplicationName();
}
