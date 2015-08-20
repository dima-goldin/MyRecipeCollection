package com.example.lenovo.myrecipecollection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class RecipeFormActivity extends ActionBarActivity {
    public static final int GET_FROM_GALLERY = 3;
    public static final String SAVEING_RECIPE = "SAVING_RECIPE";

    public int RESULT_LOAD_IMAGE;
    private ArrayList<String> ingStringList;
    private ArrayAdapter<String> arrayAdapterIngStringList;
    private   ArrayList<String> unitList;
    private ArrayAdapter<String> arrayAdapterUnitList;
    private ArrayList<Ingredient> ingredientsList;

    private String recipeName=null;
    private String recipeInstructions=null;
    private int recipeIconId=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_form_new);

        ingredientsList = new ArrayList<Ingredient>();
        ingStringList = new ArrayList<String>();
        arrayAdapterIngStringList = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,ingStringList);
        ListView listViewIngStringList = (ListView)findViewById(R.id.ingredientListForm);
        listViewIngStringList.setAdapter(arrayAdapterIngStringList);


        ListView lv = (ListView) findViewById(R.id.ingredientListForm);
        lv.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        registerForContextMenu(lv);
        Spinner spinnerUnit= (Spinner)findViewById(R.id.unitSpinner);
       unitList= new ArrayList<String>();
        arrayAdapterUnitList=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,unitList);
        arrayAdapterUnitList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter((arrayAdapterUnitList));
        for (Unit unit:Unit.values()) {
            arrayAdapterUnitList.add(unit.toString());
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu,View v,ContextMenu.ContextMenuInfo menuInfo){
        if(v.getId()!=R.id.ingredientListForm){
            return;
        }
        menu.setHeaderTitle("אפשרויות");
        String[] options = {"הסר מצרך","חזור"};

        for(String option: options)
        {
            menu.add(option);
        }

    }
    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int selectedIndex = info.position;

        if(item.getTitle().equals("הסר מצרך")){
            ingStringList.remove(selectedIndex);
            arrayAdapterIngStringList.notifyDataSetChanged();
            ingredientsList.remove(selectedIndex);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipe_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will

        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




     public void addIngToIngList(View v)
     {
         EditText amountText=(EditText)findViewById(R.id.amountEdit);
         String amountInput=amountText.getText().toString().trim();
         EditText nameText=(EditText)findViewById(R.id.IngNameEdit);
         String nameInput=nameText.getText().toString().trim();
         Spinner spinnerUnit=(Spinner)findViewById(R.id.unitSpinner);
         String unitInput=spinnerUnit.getSelectedItem().toString();
         Unit unit = Unit.EMPTY;
         for (Unit u:Unit.values()) {
             if(u.toString()==unitInput)
             {
                unit.setFieldDescription(unitInput);
             }
         }
         if(nameInput.isEmpty()){
            return;
             //todo show error to user that name is empty
         }
         if(amountInput.isEmpty()){
             Ingredient ingredient= new Ingredient(0,unit,nameInput);
             ingredientsList.add(ingredient);
             arrayAdapterIngStringList.add(ingredient.toString());

         }

        else {
             Ingredient ingredient = new Ingredient(Double.parseDouble(amountInput),unit, nameInput);
             arrayAdapterIngStringList.add(ingredient.toString());
            ingredientsList.add(ingredient);

         }
        amountText.setText("");
        nameText.setText("");

     }
    public void choosePicForRecipe(View v)
    {
        Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

//    @Override// TODO what to do if we already have an "onActivityResult"?
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
//            Uri selectedImage = data.getData();
//            String[] filePathColumn = { MediaStore.Images.Media.DATA };
//
//            Cursor cursor = getContentResolver().query(selectedImage,
//                    filePathColumn, null, null, null);
//            cursor.moveToFirst();
//
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String picturePath = cursor.getString(columnIndex);
//            cursor.close();
//
//            // String picturePath contains the path of selected Image
//        }}
   public void createAndSaveNewRecipe(View v)
   {


       EditText editRecipeName =(EditText)findViewById(R.id.editRecipeName);
       recipeName = editRecipeName.getText().toString();
       EditText editInstructions = (EditText)findViewById(R.id.editInstructions);
       recipeInstructions = editInstructions.getText().toString();
       recipeIconId=0;//change to get real picture id TODO

       if(recipeName==null|| recipeName.equals(""))
       {
           AlertDialog.Builder alert = new AlertDialog.Builder(this);
           alert.setTitle("שגיאה");
           alert.setMessage("לא הוגדר שם מתכון!");
           alert.show();
           return;
       }

       SQLiteDatabase ourDataBase=openOrCreateDatabase("ourDataBase",MODE_PRIVATE,null);
       Cursor result=ourDataBase.rawQuery("SELECT Name FROM Recipes WHERE Name='"+recipeName+"'",null);
       if(result.getCount()!=0)
       {
           AlertDialog.Builder alert = new AlertDialog.Builder(this);
           alert.setTitle("שגיאה");
           alert.setMessage("שם מתכון כבר קיים במערכת");
           alert.show();
           return;
       }
       result.close();
       ourDataBase.close();
       if(ingredientsList.isEmpty())
       {
           AlertDialog.Builder alert = new AlertDialog.Builder(this);
           alert.setTitle("שגיאה");
           alert.setMessage("לא הוכנס אף מצרך!");
           alert.show();

           return;
       }


       Intent intent=new Intent(getApplicationContext(),mainRecipeCategories.class);
       intent.putExtra(SAVEING_RECIPE,SAVEING_RECIPE);
       startActivityForResult(intent,1);

   }
   @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
   {
       super.onActivityResult(requestCode,resultCode,data);
       if(!data.getExtras().containsKey("categoryFather"))
       {
           finish();
       }
       String categoryFather=data.getExtras().getString("categoryFather");
       Recipe recipe=new Recipe(recipeName,ingredientsList,recipeInstructions,categoryFather,recipeIconId);
       SQLiteDatabase ourDataBase=openOrCreateDatabase("ourDataBase",MODE_PRIVATE,null);
       ourDataBase.execSQL("INSERT INTO Recipes(Name,Instructions,CategoryFather,IconId) VALUES('"+recipeName+"','"+recipeInstructions+"','"+categoryFather+"','"+recipeIconId+"')");
       for(Ingredient ing:ingredientsList)
       {
           String ingName=ing.getName();
           double ingAmount=ing.getAmount();
           int ingUnit=ing.getUnitInt();
           ourDataBase.execSQL("INSERT INTO Ingredients(Name,Amount,Unit,RecipeName) VALUES('"+ingName+"','"+ingAmount+"','"+ingUnit+"','"+recipeName+"')");
       }
       ourDataBase.close();
       finish();//TODO check where to return

   }






}
