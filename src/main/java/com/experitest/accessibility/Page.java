package com.experitest.accessibility;

import com.experitest.accessibility.colorthief.ContrastChecker;
import com.experitest.accessibility.colorthief.ContrastResult;
import ng.joey.lib.java.google.vision.Vision;
import ng.joey.lib.java.google.vision.entity.request.AnnotateImageRequest;
import ng.joey.lib.java.google.vision.entity.request.Feature;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Page {
    private List<Element> elementsList = new ArrayList<>();

    private List<Section> sections = new ArrayList<>();

    public List<Element> getElementsList() {
        return elementsList;
    }

    public void setElementsList(List<Element> elementsList) {
        this.elementsList = elementsList;
    }

    public List<Section> getSections() {
        return sections;
    }
    private Issue.Type[] validations = null;
    public Issue.Type[] getValidations(){
        return validations;
    }
    public HashSet<Issue.Type> getValidationsSet(){
        HashSet<Issue.Type> set = new HashSet<>();
        if(validations != null){
            for(Issue.Type type: validations){
                set.add(type);
            }
        }
        return set;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    /**
     * perform different validations
     * @param types the type of validations to perform
     * @throws IOException
     */
    public void validate(Issue.Type ...types) throws IOException {
        this.validations = types;
        for(Issue.Type type: types){
            switch (type){
                case SIZE_TO_SMALL_WIDTH:
                    for(Element element: getElementsList()){
                        if(element.getW() < 48){
                            Issue issue = new Issue();
                            issue.setType(Issue.Type.SIZE_TO_SMALL_WIDTH);
                            issue.setMessage("Element width is " + element.getW() + "px, minimum width should be 48px");
                            element.getIssues().add(issue);
                        }
                    }
                    break;
                case SIZE_TO_SMALL_HEIGHT:
                    for(Element element: getElementsList()){
                        if(element.getH() < 48){
                            Issue issue = new Issue();
                            issue.setType(Issue.Type.SIZE_TO_SMALL_HEIGHT);
                            issue.setMessage("Element height is " + element.getH() + "px, minimum height should be 48px");
                            element.getIssues().add(issue);
                        }
                    }
                    break;
                case NO_ACCESSIBILITY_INFO:
                    for(Element element: getElementsList()){
                        boolean noLabel = element.getLabel() == null || element.getLabel().isEmpty();
                        boolean textNotFound = element.getText() != null && !element.getText().isEmpty() && !Arrays.toString(element.voiceOver()).contains(element.getText());
                        if(noLabel || textNotFound){
                            Issue issue = new Issue();
                            issue.setType(Issue.Type.NO_ACCESSIBILITY_INFO);
                            if(noLabel){
                                issue.setMessage("Element doesn't have label");
                            } else {
                                issue.setMessage("Following text: " + element.getText() + " cannot be found by the screen reader");
                            }
                        }
                    }
                    break;
                case CONTRAST:
                    for(Section section: sections){
                        for(Element element: section.getElements()){
                            if(element.getW() > 0 && element.getH() > 0 && section.getImage() != null){
                                try {
                                    ContrastResult result = generateContrast(element, section.getImage());
                                    if(result == null){
                                        continue;
                                    }
                                    if((result.getRatio() < 4.5 && section.getImage2() != null)){
                                        result = generateContrast(element, section.getImage2());
                                    }
                                    if(result.getRatio() < 4.5){
                                        Issue issue = new Issue();
                                        issue.setType(Issue.Type.CONTRAST);
                                        issue.setMessage("Contrast is too low: " + (Math.round(result.getRatio() * 100)/100) + ", expected 4.5");
                                        element.getIssues().add(issue);
                                        ImageIO.write(result.getImage(), "PNG", new File("results", "out_" + System.currentTimeMillis() + ".png"));
                                    }
                                } catch (Exception ex) {
                                    System.err.println(element.voiceOver());
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                    break;
                case EXPECTED_CONTENT:
                    ContentAnalysis contentAnalysis = new ContentAnalysis(this);
                    contentAnalysis.process();
                    break;
            }
        }
    }

    private ContrastResult generateContrast(Element element, BufferedImage image){
        Rectangle elementRec = new Rectangle(element.getX(), element.getY(), element.getW(), element.getH());
        if(element.getInternalTextRec() != null){
            elementRec = element.getInternalTextRec();
        }
        Rectangle screenRec = new Rectangle(0, 0, image.getWidth(), image.getHeight());
        Rectangle elementNewRec = screenRec.intersection(elementRec);
        if(elementNewRec.width <= 0 || elementNewRec.height <= 0){
            return null;
        }
        BufferedImage newImage = new BufferedImage(elementNewRec.width, elementNewRec.height, image.getType());
        Graphics2D g = newImage.createGraphics();
        try {
            g.drawImage(image.getSubimage(elementNewRec.x, elementNewRec.y, elementNewRec.width, elementNewRec.height), 0, 0, null);
        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
            g.dispose();
        }
//                                    BufferedImage image = section.getImage().getSubimage(x, y, element.getW(), element.getH());
        ContrastResult result = ContrastChecker.findContrast(newImage);
        return result;
    }
}
