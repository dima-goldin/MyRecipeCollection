package com.example.lenovo.myrecipecollection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.EntityIterator;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toolbar;

import com.example.lenovo.myrecipecollection.ourUtilities.BitmapUtils;
import com.example.lenovo.myrecipecollection.ourUtilities.MySQLiteHelper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class RecipeFormActivity extends ActionBarActivity {
    public static final int GET_FROM_GALLERY = 3;
    public static final String SAVEING_RECIPE = "SAVING_RECIPE";


    MySQLiteHelper db;
    public int RESULT_LOAD_IMAGE;
    private ArrayList<String> ingStringList;
    private ArrayAdapter<String> arrayAdapterIngStringList;
    private   ArrayList<String> unitList;
    private ArrayAdapter<String> arrayAdapterUnitList;
    private ArrayList<Ingredient> ingredientsList;

    private String recipeName=null;
    private String recipeInstructions=null;
    private Bitmap picture;
    Recipe recipeToEdit;
    Boolean editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_form_new);
        db = new MySQLiteHelper(this);


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


        if(checkForEditMode(this))
        {
            editRecipe(recipeToEdit);
        }
    }

    private void editRecipe(Recipe recipeToEdit) {
        //TODO
        EditText title = (EditText) findViewById(R.id.editRecipeName);
        EditText instructions = (EditText) findViewById(R.id.editInstructions);
        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton);

        title.setText(recipeToEdit.getName());
        ingredientsList.addAll(recipeToEdit.getIngredientList());
        for(Ingredient ingredient : ingredientsList)
        {
            ingStringList.add(ingredient.toString());
        }
        instructions.setText(recipeToEdit.getInstructions());
        Bitmap bitMap = recipeToEdit.getPicture();
        imageButton.setImageBitmap(bitMap);
    }

    private boolean checkForEditMode(RecipeFormActivity recipeFormActivity) {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle == null)
        {
            return editMode;
        }
        String edit = bundle.getString("edit");
        if(edit != null && edit.equals("yes"))
        {
            //edit mode
            String recipeNameToEdit = bundle.getString("recipeName");
            recipeToEdit = db.getRecipe(recipeNameToEdit);
            editMode = true;
        }
        return editMode;
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
        getMenuInflater().inflate(R.menu.bar_menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menuAddCategory) {
            LayoutInflater layoutInflater=(LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            final View popupView = layoutInflater.inflate(R.layout.addcategorypopup,null);
            final PopupWindow popupWindow= new PopupWindow(popupView, Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
            Button cancelPopUpButton = (Button)popupView.findViewById(R.id.cancelPopUpButton);
            cancelPopUpButton.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View v){
                    popupWindow.dismiss();
                }
            });
            popupWindow.setFocusable(true);
            popupWindow.showAsDropDown(findViewById(R.id.CreateRecipeTitle));
            //popupWindow.showAtLocation(this.g, Gravity.CENTER,0,0);

            Button submitButton=(Button)popupView.findViewById(R.id.popUpSubmitCategoryButton);
            submitButton.setOnClickListener(new Button.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    EditText nameText = (EditText) popupView.findViewById(R.id.editPopUpCategoryName);
                                                    String newCategoryName = nameText.getText().toString();
                                                    //TODO this is only temporary, i need to get the bitmap from gallery/camera and turn into a bitmap
                                                    Bitmap picture = BitmapUtils.drawableToBitmap(getResources().getDrawable(R.drawable.notavaliable));
                                                    db.insertCategory(newCategoryName,null,picture);
                                                    popupWindow.dismiss();


                                                }

                                            }


            );

        }
        if(id==R.id.menuAddRecipe)
        {
            return true;
        }
        if(id==R.id.menuReturnToMainPage)
        {
            startActivity(new Intent(this,MainActivity.class));
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
       picture = BitmapUtils.drawableToBitmap(getResources().getDrawable(R.drawable.notavaliable));//TODO change to get real picture

       if(recipeName==null|| recipeName.equals(""))
       {
           AlertDialog.Builder alert = new AlertDialog.Builder(this);
           alert.setTitle("שגיאה");
           alert.setMessage("לא הוגדר שם מתכון!");
           alert.show();
           return;
       }

       Recipe recipe = db.getRecipe(recipeName);
       if(recipe != null && !editMode)
       {
           AlertDialog.Builder alert = new AlertDialog.Builder(this);
           alert.setTitle("שגיאה");
           alert.setMessage("שם מתכון כבר קיים במערכת");
           alert.show();
           return;
       }

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

       if(data == null)
       {
           return;
       }
       if(!data.getExtras().containsKey("categoryFather"))
       {
           finish();
       }
       String categoryFather=data.getExtras().getString("categoryFather");
       Recipe recipe=new Recipe(recipeName,ingredientsList,recipeInstructions,categoryFather,picture);
       if(editMode)
       {
           db.deleteRecipeIngredients(recipeToEdit.getName());
           db.deleteRecipe(recipeToEdit.getName());
       }
       db.insertRecipe(recipeName,recipeInstructions,categoryFather,picture);
       for(Ingredient ing:ingredientsList)
       {
           String ingName=ing.getName();
           double ingAmount=ing.getAmount();
           int ingUnit=ing.getUnitInt();
           db.insertIngredient(ingName,ingAmount,ingUnit,recipeName);
       }
       Intent intent = new Intent(this, MainActivity.class);
       startActivity(intent);
       finish();//TODO check where to return

   }






}
