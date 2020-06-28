package com.experitest.accessibility;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

public class Defaults {
    private static Defaults instance = null;
    public static synchronized Defaults getInstance(){
        if(instance == null){
            instance = new Defaults();
        }
        return instance;
    }

    private int minWidth = 48;
    private int minHeight = 48;
    private double smallTextContrast = 3.5;
    private double largeTextContrast = 3.5;
    private int contrastBreak = 24;


    private Defaults(){
        File propertiesFile = new File("accessibility.properties");
        Properties properties = new Properties();
        if(propertiesFile.exists()){
            try(FileReader fileReader = new FileReader(propertiesFile)){
                properties.load(fileReader);
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
        if(properties.containsKey("min.width")){
            minWidth = Integer.parseInt(properties.getProperty("min.width"));
        }
        if(properties.containsKey("min.height")){
            minHeight = Integer.parseInt(properties.getProperty("min.height"));
        }
        if(properties.containsKey("small.contrast")){
            smallTextContrast = Double.parseDouble(properties.getProperty("small.contrast"));
        }
        if(properties.containsKey("large.contrast")){
            largeTextContrast = Double.parseDouble(properties.getProperty("large.contrast"));
        }
        if(properties.containsKey("contrast.break")){
            contrastBreak = Integer.parseInt(properties.getProperty("contrast.break"));
        }
    }
    public int getMinWidth() {
        return minWidth;
    }

    public int getMinHeight() {
        return minHeight;
    }

    public double getSmallTextContrast() {
        return smallTextContrast;
    }

    public double getLargeTextContrast() {
        return largeTextContrast;
    }
    public int getContrastBreak() {
        return contrastBreak;
    }


}
