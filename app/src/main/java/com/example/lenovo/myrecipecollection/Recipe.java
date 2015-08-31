package com.example.lenovo.myrecipecollection;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by Lenovo on 24/2/2015.
 */
public class Recipe extends Category {
    private List<Ingredient> ingredientList;
    private String instructions;

    public Recipe(String name, List<Ingredient> ingredientList, String instructions,String parentCategory,Bitmap picture) {

        super(name,picture,parentCategory,"מתכון");
        this.ingredientList = ingredientList;
        this.instructions = instructions;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ingredient> getIngredientList() {
        return ingredientList;
    }

    public void setIngredientList(List<Ingredient> ingredientList) {
        this.ingredientList = ingredientList;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }


}
