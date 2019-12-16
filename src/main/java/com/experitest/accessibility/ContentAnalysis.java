package com.experitest.accessibility;

import com.google.gson.Gson;
import ng.joey.lib.java.google.vision.Vision;
import ng.joey.lib.java.google.vision.entity.request.AnnotateImageRequest;
import ng.joey.lib.java.google.vision.entity.request.Feature;
import ng.joey.lib.java.google.vision.entity.request.Image;
import ng.joey.lib.java.google.vision.entity.response.AnnotateImageResponse;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Properties;
import java.util.regex.Pattern;

public class ContentAnalysis {
    private Page page;
    public ContentAnalysis(Page page){
        this.page = page;
    }

    public void process() throws IOException{
        File googleVisionPropertiesFile = new File("googlevision.properties");
        if(!googleVisionPropertiesFile.exists()){
            throw new RuntimeException("Unable to find googlevision.properties file. This file is required for testing of EXPECTED_CONTENT");
        }
        Properties googleVisionProperties = new Properties();
        try (FileReader fileReader = new FileReader(googleVisionPropertiesFile)){
            googleVisionProperties.load(fileReader);
        }
        String googleVisionKey = googleVisionProperties.getProperty("key");
        if(googleVisionKey == null){
            throw new RuntimeException("Couldn't find key property in the googlevision.properties file");
        }
        Vision vision = new Vision();
        for(Section section: page.getSections()) {
            Image image = new Image(imgToBase64String(section.getImage(), "PNG"));
            Vision.Response response = Vision.analyze(getRequest(image), googleVisionKey);
            String resp = new Gson().toJson(response);
            System.out.println(resp);
            if(response.responses == null || response.responses.length == 0){
                throw new RuntimeException("Could not get response");
            }
            String[] textValues = response.responses[0].fullTextAnnotation.text.split("\n");
            HashSet<String> notFound = new HashSet<>();
            for(int i = 0; i < textValues.length; i++){
                String text = textValues[i];
                if(i < 4) { // first few elements can contain the header
                    if (text.equalsIgnoreCase("No SIM")) {
                        // part of header
                        continue;
                    }
                    if (Pattern.matches("\\d\\d\\:\\d\\d", text)) {
                        continue;
                    }
                }
                boolean found = false;
                for(Element el: page.getElementsList()){
                    String voiceOver = Arrays.toString(el.voiceOver()).toLowerCase().replace('0', 'o');
                    if(voiceOver.contains(text.toLowerCase().replace('0', 'o'))){
                        found = true;
                        break;
                    }
                }
                if(!found && !notFound.contains(text)){
                    notFound.add(text);
                    Issue issue = new Issue();
                    issue.setType(Issue.Type.EXPECTED_CONTENT);
                    issue.setMessage("The text: " + text + " was identify using OCR but wasn't found in the page");
                    section.getIssues().add(issue);
                }

            }

        }

    }
    private static String imgToBase64String(final RenderedImage img, final String formatName) {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, formatName, os);
            return Base64.getEncoder().encodeToString(os.toByteArray());
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }
    private static Vision.Request getRequest(Image image){
        Vision.Request request = new Vision.Request(
                //embed a list of AnnotateImageRequest's
                new AnnotateImageRequest[]{
                        new AnnotateImageRequest(
                                //add an image source
                                image,
                                //and a list of features to detect
                                new Feature[]{
                                        new Feature(Feature.Type.DOCUMENT_TEXT_DETECTION)
                                }
                        )
                }
        );
        return request;
    }
}
