package com.experitest.accessibility;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Element {
    @SerializedName(value = "ElementHash")
    private String elementHash;
    @SerializedName(value = "Label")
    private String label;
    @SerializedName(value = "Class")
    private String clazz;
    @SerializedName(value = "Traits")
    private String traits;
    @SerializedName(value = "Identifier")
    private String identifier;
    private int x = -1;
    private int y = -1;
    private int w = -1;
    private int h = -1;
    private String placeholder;
    private String dumpClass;
    private String value;
    private String internalElementText;
    private String text;
    private Rectangle internalTextRec;

    public Rectangle getInternalTextRec() {
        return internalTextRec;
    }

    public void setInternalTextRec(Rectangle internalTextRec) {
        this.internalTextRec = internalTextRec;
    }

    private ArrayList<Issue> issues = new ArrayList<>();

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getInternalElementText() {
        return internalElementText;
    }

    public void setInternalElementText(String internalElementText) {
        this.internalElementText = internalElementText;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDumpClass() {
        return dumpClass;
    }

    public void setDumpClass(String dumpClass) {
        this.dumpClass = dumpClass;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getElementHash() {
        return elementHash;
    }

    public void setElementHash(String elementHash) {
        this.elementHash = elementHash;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getTraits() {
        return traits;
    }

    public void setTraits(String traits) {
        this.traits = traits;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public ArrayList<Issue> getIssues() {
        return issues;
    }

    public void setIssues(ArrayList<Issue> issues) {
        this.issues = issues;
    }

    public String[] voiceOver(){
        ArrayList<String> list = new ArrayList<>();
        if(label != null && !label.isEmpty()){
            list.add(label);
        }
        if(clazz != null && clazz.equals("UIAccessibilityElementKBKey")) {

        } else {
            if(placeholder != null && !placeholder.isEmpty() && !placeholder.equalsIgnoreCase(label)){
                list.add(placeholder);
            }
            if(traits != null && !traits.isEmpty()){
                String[] traitsElements = traits.split(";");
                boolean dim = false;
                boolean button = false;
                boolean staticText = false;
                boolean tab = false;
                for(String v: traitsElements){
                    if("Not Enabled".equalsIgnoreCase(v)){
                        dim = true;
                    } else if ("button".equalsIgnoreCase(v)){
                        button = true;
                    } else if("static text".equalsIgnoreCase(v)){
                        staticText = true;
                    } else if("tab".equalsIgnoreCase(v)){
                        tab = true;
                    }
                }
                boolean justStatic = staticText && traitsElements.length == 1;
                if(value != null && !value.equals(label) && button && !value.equalsIgnoreCase(placeholder)) {
                    list.add(value);
                }

                if(internalElementText != null){
                    list.add(internalElementText);
                }
                if(dim){
                    list.add("dim");
                }
                if(traitsElements.length == 1 && !staticText){
                    list.add(traits);
                } else if(tab){
                    list.add("Tab");
                } else if(button){
                    list.add("Button");
                }

            } else if(dumpClass != null && !dumpClass.isEmpty()){
                String value = dumpClass;
                if(value.startsWith("XCUIElementType")){
                    value = value.substring("XCUIElementType".length());
                }
                list.add(value);
            }
        }
        return list.toArray(new String[0]);
    }
    public String buildXPath(){
        StringBuffer buf = new StringBuffer();
        buf.append("//*[");
        boolean hasIdentification = false;
        boolean first = true;
        if(identifier != null){
            buf.append("@id='");
            buf.append(identifier);
            buf.append("'");
            first = false;
            hasIdentification = true;
        }
        if(label != null){
            if(!first){
                buf.append(" and ");
            }
            if(label.contains(",")){
                buf.append("contains(@label, '");
                buf.append(label.split(",")[0]);
                buf.append("')");
            } else {
                buf.append("@label='");
                buf.append(label);
                buf.append("'");
            }
            first = false;
            hasIdentification = true;
        }
        if(traits != null && !traits.isEmpty() && first && "Search Field".equals(traits)){
            buf.append("@XCElementType='XCUIElementTypeSearchField'");
            hasIdentification = true;
        }
        buf.append("]");
        if(!hasIdentification){
            return null;
        }
        return buf.toString();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public boolean equals(Object o){
        if(o == null || !(o instanceof Element)){
            return false;
        }
        Element el = (Element)o;
        if(getElementHash() == null || el.getElementHash() == null){
            return false;
        }
        return getElementHash().equals(el.getElementHash());
    }
    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append(new Gson().toJson(this));
        buffer.append("\n");
        buffer.append("Label: " + label);
        buffer.append("\n");
        buffer.append("VoiceOver: " + Arrays.toString(voiceOver()));
        buffer.append("\nIssues:\n");
        if(issues.size() == 0){
            buffer.append("None\n");
        } else {
            for(Issue issue: issues){
                buffer.append(issue.getType());
                buffer.append(": ");
                buffer.append(issue.getMessage());
                buffer.append("\n");
            }
        }
        return buffer.toString();
    }

}
