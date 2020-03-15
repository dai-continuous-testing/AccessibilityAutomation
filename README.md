# Accessibility Automation
This project enable you to execute accessibility tests on your iOS devices.
To use it you should use SeeTest Appium cloud in version 12.12 or higher.

Tests Types:

SIZE_TOO_SMALL_WIDTH - Check elements width are at least 48px.

SIZE_TOO_SMALL_HEIGHT - Check elements height are at least 48px.

NO_ACCESSIBILITY_INFO - Elements are missing accessibility information.

CONTRAST - Color contrast is at least 4.5.

EXPECTED_CONTENT - Check page content.

CONTENT_ORDER - Check page navigation order.

IMPORTANT_NO_ACCESSIBILITY - Important elements without accessibility.


```java
Page page = AccessibilityUtils.getPageAccessibilityInformation(driver, "com.apple.mobiletimer", 70, true);
page.validate(Issue.Type.SIZE_TOO_SMALL_HEIGHT, Issue.Type.SIZE_TOO_SMALL_WIDTH, Issue.Type.CONTRAST, Issue.Type.NO_ACCESSIBILITY_INFO, Issue.Type.IMPORTANT_NO_ACCESSIBILITY);
HtmlReportGenerator.generateReport(page, "Clock", new File("results"));

```

We would like to thanks the following projects, that were used for color contrast calculations as well as integration with googlevision:
https://github.com/Tanaguru/Contrast-Finder

https://github.com/SvenWoltmann/color-thief-java

https://github.com/josephdalughut/googlecloudvision-api-java
