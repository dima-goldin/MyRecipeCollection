package com.example.lenovo.myrecipecollection;


public class Category {

    protected String name;
    protected Integer iconID;
    protected String parent;
    protected String description;


    public Category(String name, Integer iconID, String parent,String description) {
        this.name = name;
        this.iconID = iconID;
        this.parent = parent;
        this.description=description;

    }
    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public Integer getIconID() {
        return iconID;
    }

    public void setIconID(Integer iconID) {
        this.iconID = iconID;
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
