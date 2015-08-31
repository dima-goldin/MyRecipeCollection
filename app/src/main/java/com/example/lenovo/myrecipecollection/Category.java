package com.example.lenovo.myrecipecollection;


import android.graphics.Bitmap;

import com.example.lenovo.myrecipecollection.ourUtilities.BitmapUtils;

public class Category {

    protected String name;
    protected Bitmap picture;
    protected String parent;
    protected String description;


    public Category(String name, Bitmap bitmap, String parent,String description) {
        this.name = name;
        this.picture = bitmap;
        this.parent = parent;
        this.description=description;

    }
    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



}
