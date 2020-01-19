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
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class ContentAnalysis {
    private Page page;
    private String googleVisionKey;
    public ContentAnalysis(Page page){
        this.page = page;
    }

    public void verifyKey() throws IOException{
        File googleVisionPropertiesFile = new File("googlevision.properties");
        if(!googleVisionPropertiesFile.exists()){
            throw new RuntimeException("Unable to find googlevision.properties file. This file is required for testing of EXPECTED_CONTENT");
        }
        Properties googleVisionProperties = new Properties();
        try (FileReader fileReader = new FileReader(googleVisionPropertiesFile)){
            googleVisionProperties.load(fileReader);
        }
        googleVisionKey = googleVisionProperties.getProperty("key");
        if(googleVisionKey == null){
            throw new RuntimeException("Couldn't find key property in the googlevision.properties file");
        }
    }

    public void initSections() throws InterruptedException{
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(page.getSections().size());
        for(Section section: page.getSections()){
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    Image image = new Image(imgToBase64String(section.getImage(), "PNG"));
                    Vision.Response response = null;
                    try {
                        response = Vision.analyze(getRequest(image), googleVisionKey);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    section.setVisionResponse(response);
                }
            };
            executor.execute(task);
        }
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
    }

    public void process() throws IOException{

        HashSet<String> notFound = new HashSet<>();
        for(Section section: page.getSections()) {
            Vision.Response response = section.getVisionResponse();
            String resp = new Gson().toJson(response);
            System.out.println(resp);
            if(response.responses == null || response.responses.length == 0){
                throw new RuntimeException("Could not get response");
            }
            String[] textValues = response.responses[0].fullTextAnnotation.text.split("\n");
            for(int i = 0; i < textValues.length; i++){
                String text = textValues[i];
                if(text.length() == 1){ // ignore single char texts
                    continue;
                }
                String[] textElements = text.split("\\s+");
                StringBuilder sb = new StringBuilder();
                for(String textElement: textElements){
                    if(textElement.length() == 1 || Pattern.matches("\\W+", textElement)){
                        continue;
                    }
                    sb.append(textElement);
                    sb.append(" ");
                }
                if(sb.toString().isEmpty()){
                    continue;
                }
                String textAlternative = sb.toString().trim();
                if(i < 4) { // first few elements can contain the header
                    if (text.equalsIgnoreCase("No SIM")) {
                        // part of header
                        continue;
                    }
                    if (Pattern.matches("\\d+\\:\\d\\d", text)) {
                        continue;
                    }
                }
                boolean found = false;
                for(Element el: page.getElementsList()){
                    String voiceOver = Arrays.toString(el.voiceOver()).toLowerCase().replace('0', 'o');
                    if(voiceOver.contains(text.toLowerCase().replace('0', 'o')) || voiceOver.contains(textAlternative.toLowerCase().replace('0', 'o'))){
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
                                        ,new Feature(Feature.Type.TEXT_DETECTION)
                                }
                        )
                }
        );
        return request;
    }
}
