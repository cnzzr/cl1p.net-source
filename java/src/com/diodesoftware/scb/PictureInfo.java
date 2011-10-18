package com.diodesoftware.scb;

/**
 * Copyright 2008 Sensemaker Software Inc.
 * User: rob
 * Date: Mar 16, 2008
 * Time: 11:09:20 AM
 */
public class PictureInfo {

    private String url;
    private int width;
    private int height;
    private String name;
    private int smallWidth;
    private int smallHeight;
    private int number;

    public PictureInfo(int number, String url, int width, int height, String name) {
        this.number = number;
        this.url = url;
        this.width = width;
        this.height = height;
        this.name = name;
        if(width > 700 && height > 400){
        this.smallWidth = 700;
            float percent = 700f / (float)width;
            float f = (float)height * percent;
            smallHeight = (int)(f);
        }else{
            smallWidth = width;
            smallHeight = height;
        }

    }

    public String getUrl() {
        return url;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getName() {
        return name;
    }

    public int getSmallWidth() {
        return smallWidth;
    }

    public int getSmallHeight() {
        return smallHeight;
    }

    public int getNumber() {
        return number;
    }
}
