package com.example.lenovo.myrecipecollection.ourUtilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.lenovo.myrecipecollection.Category;
import com.example.lenovo.myrecipecollection.Ingredient;
import com.example.lenovo.myrecipecollection.Recipe;
import com.example.lenovo.myrecipecollection.Unit;

import java.util.ArrayList;

public class MySQLiteHelper extends SQLiteOpenHelper {
    public static final String TAG = "DB";
    public static final String DATABASE_NAME = "ourDataBase";
    public static final int DATABASE_VERSION = 3;
    public static final String TABLE_CATEGORIES = "Categories";
    public static final String TABLE_INGREDIENTS = "Ingredients";
    public static final String TABLE_RECIPES = "Recipes";
    public static final String CAT_COL_NAME = "Name";
    public static final String CAT_COL_CATEGORY_FATHER = "CategoryFather";
    public static final String CAT_COL_ICON_ID = "IconId";
    public static final String ING_COL_ID = "ID";
    public static final String ING_COL_NAME = "Name";
    public static final String ING_COL_AMOUNT = "Amount";
    public static final String ING_COL_UNIT = "Unit";
    public static final String ING_COL_RECIPE_NAME = "RecipeName";
    public static final String RECIPE_COL_NAME = "Name";
    public static final String RECIPE_COL_INSTRUCTIONS = "Instructions";
    public static final String RECIPE_COL_CATEGORY_FATHER = "CategoryFather";
    public static final String RECIPE_COL_ICON_ID = "IconId";



    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG,"onCreate");
        String statmentCreateTableCategories = "CREATE TABLE "+TABLE_CATEGORIES + "(" + CAT_COL_NAME + " text not null primary key, " + CAT_COL_CATEGORY_FATHER + " text," + CAT_COL_ICON_ID + " integer);";
        String statmentCreateTableRecipes = "CREATE TABLE " + TABLE_RECIPES + " (" + RECIPE_COL_NAME + " text not null primary key, " + RECIPE_COL_INSTRUCTIONS + " text, " + RECIPE_COL_CATEGORY_FATHER + ", " + RECIPE_COL_ICON_ID  + " integer);";
        String statmentCreateTableIngredients = "CREATE TABLE " + TABLE_INGREDIENTS + " (" + ING_COL_ID + " integer not null primary key autoincrement, " + ING_COL_NAME + " text not null, " + ING_COL_AMOUNT + " double, " + ING_COL_UNIT + " integer, " + ING_COL_RECIPE_NAME + " text not null);";
        db.execSQL(statmentCreateTableCategories);
        db.execSQL(statmentCreateTableRecipes);
        db.execSQL(statmentCreateTableIngredients);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES + ", " + TABLE_INGREDIENTS + ", " + TABLE_RECIPES + ";");
        onCreate(db);
    }

    public long insertCategory(String name, String categoryFather, int iconId)
    {
        Log.d(TAG, "insertCategory Name: " + name);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CAT_COL_NAME, name);
        values.put(CAT_COL_CATEGORY_FATHER,categoryFather);
        values.put(CAT_COL_ICON_ID, iconId);
        long id = db.insertOrThrow(TABLE_CATEGORIES, null, values);
        return id;

    }

    public Category getCategory(String name)
    {
        Log.d(TAG,"getCategory Name: " + name);
        SQLiteDatabase db = getReadableDatabase();
        String stament = "SELECT * FROM " + TABLE_CATEGORIES + " WHERE " + CAT_COL_NAME + "='" + name +"';";


        Cursor cursor = db.rawQuery(stament, null);

        if(cursor.isAfterLast())
        {
            return null;
        }
        cursor.moveToFirst();
        Category category = new Category(cursor.getString(0),cursor.getInt(2),cursor.getString(1),"");
        return category;
    }

    public ArrayList<Category> getCategoriesByFather(String parentCategory)
    {
        Log.d(TAG, "getCategoriesByFather parentCategory: " + parentCategory);

        ArrayList<Category> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String stament = "SELECT * FROM " + TABLE_CATEGORIES + " WHERE " + CAT_COL_CATEGORY_FATHER + "='" + parentCategory +"';";
        String altStament = "SELECT * FROM " + TABLE_CATEGORIES + " WHERE " + CAT_COL_CATEGORY_FATHER + " IS NULL;";

        Cursor cursor;
        if(parentCategory == null) {
         cursor = db.rawQuery(altStament, null);
        }
        else
        {
            cursor = db.rawQuery(stament,null);
        }

        if(cursor.isAfterLast())
        {
            return null;
        }

        while(cursor.moveToNext())
        {
            Category category = new Category(cursor.getString(0),cursor.getInt(2),cursor.getString(1),"");
            list.add(category);
        }
        cursor.close();

        return list;
    }

    public void deleteCategory(String name)
    {
        Log.d(TAG, "deleteCategory Name: " + name);

        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_CATEGORIES, CAT_COL_NAME + " = ?", new String[]{name});
    }

    public long insertIngredient(String name, double amount, int unit, String recipeName)
    {
        Log.d(TAG, "insertIngredient Name: " + name);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ING_COL_NAME, name);
        values.put(ING_COL_AMOUNT,amount);
        values.put(ING_COL_UNIT, unit);
        values.put(ING_COL_RECIPE_NAME, recipeName);
        long id = db.insertOrThrow(TABLE_INGREDIENTS, null, values);
        return id;

    }

    public ArrayList<Ingredient> getRecipeIngredients(String recipeName)
    {
        Log.d(TAG, "getRecipeIngredients recipeName: " +recipeName );
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Ingredient> list = new ArrayList<>();

        String statment = "SELECT * FROM " + TABLE_INGREDIENTS + " WHERE " + ING_COL_RECIPE_NAME+"='"+recipeName+"';";

        Cursor cursor = db.rawQuery(statment, null);

        if(cursor.isAfterLast())
        {
            return null;
        }

        while(cursor.moveToNext())
        {
            Ingredient ingredient = new Ingredient(cursor.getDouble(2), Unit.returnUnitByInt(cursor.getInt(3)), cursor.getString(1));
            list.add(ingredient);
        }
        return list;

    }

    public void deleteRecipeIngredients(String recipeName)
    {
        Log.d(TAG, "deleteRecipeIngredients RecipeName: " + recipeName);

        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_CATEGORIES, RECIPE_COL_NAME + " = ?",new String[] {recipeName});
    }


    public long insertRecipe(String name, String instructions, String categoryFather, int iconId)
    {
        Log.d(TAG, "insertRecipe Name: " + name);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RECIPE_COL_NAME, name);
        values.put(RECIPE_COL_INSTRUCTIONS,instructions);
        values.put(RECIPE_COL_CATEGORY_FATHER, categoryFather);
        values.put(RECIPE_COL_ICON_ID, iconId);
        long id = db.insertOrThrow(TABLE_RECIPES, null, values);
        return id;

    }

    public Recipe getRecipe(String name)
    {
        Log.d(TAG, "getRecipe Name: " + name);

        SQLiteDatabase db = getReadableDatabase();

        String statment = "SELECT * FROM " + TABLE_RECIPES + " WHERE " + RECIPE_COL_NAME + "='" + name + "';";

        Cursor cursor = db.rawQuery(statment,null);

        if(cursor.isAfterLast())
        {
            return null;
        }
        cursor.moveToFirst();
        Recipe recipe = new Recipe(name, null,cursor.getString(1), cursor.getString(2),cursor.getInt(3));
        cursor.close();
        ArrayList<Ingredient> list = getRecipeIngredients(name);
        recipe.setIngredientList(list);

        return recipe;
    }

    public ArrayList<Recipe> getRecipesByCategory(String categoryFather)
    {
        Log.d(TAG, "getRecipesByCategory categoryFather: " + categoryFather);

        SQLiteDatabase db = getReadableDatabase();

        String statment = "SELECT * FROM " +TABLE_RECIPES + " WHERE " + RECIPE_COL_CATEGORY_FATHER + "='" + categoryFather + "';";
        String altStatment = "SELECT * FROM " +TABLE_RECIPES + " WHERE " + RECIPE_COL_CATEGORY_FATHER + " IS NULL;";

        Cursor cursor;
        if(categoryFather == null)
        {
            cursor = db.rawQuery(altStatment,null);
        }
        else
        {
            cursor = db.rawQuery(statment, null);
        }


        if(cursor.isAfterLast())
        {
            return null;
        }

        ArrayList<String> recipeNames = new ArrayList<>();
        while(cursor.moveToNext())
        {
            recipeNames.add(cursor.getString(0));
        }

        ArrayList<Recipe> recipes = new ArrayList<>();
        for(String name : recipeNames)
        {
            recipes.add(getRecipe(name));
        }

        return recipes;
    }

    public void deleteRecipe(String name)
    {
        Log.d(TAG, "deleteRecipe Name: " + name);

        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_RECIPES, RECIPE_COL_NAME + " = ?",new String[] {name});
    }

}


