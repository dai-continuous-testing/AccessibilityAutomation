package com.experitest.accessibility;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.print.Doc;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;

public class AccessibilityUtils {
    public static Page getPageAccessibilityInformation(RemoteWebDriver driver, String appBundleId, int maxElements, boolean startStop) throws Exception{
        if(maxElements <= 0){
            maxElements = 100;
        }
        Page page = new Page();
        if(startStop){
            driver.executeScript("seetest:client.accessibilityStart()");
        }

        driver.executeScript("seetest:client.launch(\"" + appBundleId + "\", \"false\",\"false\")");

        String xml = driver.getPageSource();
        Document doc = convertStringToXMLDocument(xml);

        //(String)driver.executeScript("seetest:client.getVisualDump(\"Native\")");
        System.out.println(xml);
                //getPageSource();
        HashSet<String> elementsHash = new HashSet<>();
//        String xml = driver.getPageSource();
        Section currentSection = new Section();
        currentSection.setDump(doc);
        currentSection.setImage(getScreenshot(driver));
        page.getSections().add(currentSection);
        boolean secondImageNeeded = true;
        for(int i = 0; i < maxElements; i++){
            String str4 = (String)driver.executeScript("seetest:client.accessibilityMoveNext()");
            if(secondImageNeeded){
                currentSection.setImage2(getScreenshot(driver));
                secondImageNeeded = false;
            }

            JsonObject json = JsonParser.parseString(str4).getAsJsonObject();
            String text = json.get("text").getAsString();
            Element el = new Gson().fromJson(text, Element.class);
            if(elementsHash.contains(el.getElementHash())){
                break;
            }
            elementsHash.add(el.getElementHash());
            page.getElementsList().add(el);
            try {
                boolean status = updateLocations(doc, el);
                if(!status){
                    System.out.println("Get new page");
                    xml = driver.getPageSource();
                    doc = convertStringToXMLDocument(xml);
                    status = updateLocations(doc, el);
                    if(!status) {
                        System.err.println("Fail to identify location in the second try: " + el.buildXPath());
                    }
                    currentSection = new Section();
                    currentSection.setDump(doc);
                    currentSection.setImage(getScreenshot(driver));
                    secondImageNeeded = true;
                    page.getSections().add(currentSection);
                    currentSection.getElements().add(el);
                } else {
                    currentSection.getElements().add(el);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(startStop) {
            driver.executeScript("seetest:client.accessibilityStop()");
        }

        return page;
    }


    private static boolean updateLocations(Document doc, Element el) throws Exception{
        XPath xPath = XPathFactory.newInstance().newXPath();
        String xpStr = el.buildXPath();
        if(xpStr != null) {
            NodeList nodeList = (NodeList)xPath.compile(xpStr).evaluate(doc, XPathConstants.NODESET);
            if(nodeList.getLength() == 0){
                System.err.println("No element found: " + el.toString());
                return false;
            } else if(nodeList.getLength() > 1){
                System.out.println("Found multiple elements for xpath: " + xpStr + ", Element: " + el.toString());
            }
            org.w3c.dom.Element element = (org.w3c.dom.Element)nodeList.item(0);
            el.setX(Integer.parseInt(element.getAttribute("x")));
            el.setY(Integer.parseInt( element.getAttribute("y")));
            el.setW(Integer.parseInt(element.getAttribute("width")));
            el.setH(Integer.parseInt(element.getAttribute("height")));
            String placeholder = element.getAttribute("placeholder");
            el.setDumpClass(element.getAttribute("XCElementType"));
            el.setValue(element.getAttribute("value"));
            el.setText(element.getAttribute("text"));
            el.setPlaceholder(placeholder);
            // check internal switch
            //
            nodeList = (NodeList)xPath.compile(xpStr + "/*[@knownSuperClass='UISwitch']").evaluate(doc, XPathConstants.NODESET);
            if(nodeList.getLength() > 0){
                if("0".equals(((org.w3c.dom.Element) nodeList.item(0)).getAttribute("value"))){
                    el.setInternalElementText("OFF");
                } else {
                    el.setInternalElementText("ON");
                }
            }
            //*[@XCElementType='XCUIElementTypeStaticText']
            nodeList = (NodeList)xPath.compile(xpStr + "/*[@XCElementType='XCUIElementTypeStaticText' and @text='" + el.getLabel() + "']").evaluate(doc, XPathConstants.NODESET);
            if(nodeList.getLength() > 0) {
                org.w3c.dom.Element element1 = (org.w3c.dom.Element)nodeList.item(0);
                Rectangle rectangle = new Rectangle();
                rectangle.setBounds(Integer.parseInt(element1.getAttribute("x")), Integer.parseInt(element1.getAttribute("y")), Integer.parseInt(element1.getAttribute("width")), Integer.parseInt(element1.getAttribute("height")));
                el.setInternalTextRec(rectangle);
            }
                return true;
        } else {
            System.err.println("No xpath to: " + el.toString());
        }
        return false;
    }

    private static Document convertStringToXMLDocument(String xmlString) throws IOException, SAXException, ParserConfigurationException {
        //Parser that produces DOM object trees from XML content
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        //API to obtain DOM Document instance
        DocumentBuilder builder = null;
        //Create DocumentBuilder with default configuration
        builder = factory.newDocumentBuilder();

        //Parse the content to Document object
        Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
        return doc;
    }
    private static BufferedImage getScreenshot(RemoteWebDriver driver) throws IOException{
        ByteArrayInputStream imgbytes = new ByteArrayInputStream(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
        assert imgbytes.available() > 0;
        BufferedImage bimg = ImageIO.read(imgbytes);
        return bimg;
    }
}
