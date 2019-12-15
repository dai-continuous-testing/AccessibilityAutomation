package com.experitest.accessibility;

import java.awt.image.BufferedImage;

public class Issue {
    enum Type {
        SIZE_TO_SMALL_WIDTH("Check elements width are at least 48px"),
        SIZE_TO_SMALL_HEIGHT("Check elements height are at least 48px"),
        NO_ACCESSIBILITY_INFO("Elements are missing accessibility information"),
        CONTRAST("Color contrast is at least 4.5"),
        EXPECTED_CONTENT("Check page content"),
        CONTENT_ORDER("Check page navigation order"),
        IMPORTANT_NO_ACCESSIBILITY("Important elements without accessibility");

        Type(String description){
            this.description = description;
        }
        String description;
        String getDescription(){
            return description;
        }
    }
    private Type type;
    private String message;
    private BufferedImage image;

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
