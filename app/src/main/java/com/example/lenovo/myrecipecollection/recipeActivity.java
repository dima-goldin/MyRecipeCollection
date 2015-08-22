package com.example.lenovo.myrecipecollection;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;


public class recipeActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        String recipeName=getIntent().getExtras().getString("key");
        showRecipe(recipeName);

    }

    private void showRecipe(String recipeName) {
        SQLiteDatabase ourDataBase=openOrCreateDatabase("ourDataBase",MODE_PRIVATE,null);
        Cursor recipeCursor;
        recipeCursor=ourDataBase.rawQuery("SELECT * FROM Recipes WHERE Name='"+ recipeName+"'",null);
        recipeCursor.moveToFirst();
        if(recipeCursor==null)
        {
            // Error TODO
            return;
        }
        Cursor ingsCursor;

        ingsCursor=ourDataBase.rawQuery("SELECT Name,Amount,Unit FROM Ingredients WHERE RecipeName='"+recipeName+"'",null);
        String recipeInstructions=recipeCursor.getString(1);
        String recipeFatherCategory=recipeCursor.getString(2);
        int recipeIconId=recipeCursor.getInt(3);

        List<Ingredient> ingredientList=new ArrayList<Ingredient>();
        while(ingsCursor.moveToNext())
        {
            String Name=ingsCursor.getString(0);
            double Amount=ingsCursor.getDouble(1);
            Unit unit=Unit.returnUnitByInt(ingsCursor.getInt(2));
            ingredientList.add(new Ingredient(Amount,unit,Name));
        }

        ourDataBase.close();
        fillRecipe(recipeName,recipeInstructions,recipeIconId,ingredientList,recipeFatherCategory);


    }
    private void fillRecipe(String name,String instructions,int iconId,List<Ingredient> ingredientList,String father)
    {
        TextView nameView= (TextView) findViewById(R.id.showRecipeNameTitle);
        nameView.setText(name);

        TextView instructionsView=(TextView)findViewById(R.id.showInstructions);
        instructionsView.setText(instructions);
        ImageView iconView=(ImageView)findViewById(R.id.showIcon);
        iconView.setImageResource(iconId);
        String ingsString=new String();
        for(Ingredient ing:ingredientList)
        {
            ingsString=ingsString+ing.toString()+"\n";
        }
        TextView ingsView=(TextView)findViewById(R.id.showIngs);
        ingsView.setText(ingsString);
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
            popupWindow.showAsDropDown(findViewById(R.id.showRecipeNameTitle));
            //popupWindow.showAtLocation(this.g, Gravity.CENTER,0,0);

            Button submitButton=(Button)popupView.findViewById(R.id.popUpSubmitCategoryButton);
            submitButton.setOnClickListener(new Button.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    EditText nameText = (EditText) popupView.findViewById(R.id.editPopUpCategoryName);
                                                    String newCategoryName = nameText.getText().toString();
                                                    SQLiteDatabase ourDataBase = openOrCreateDatabase("ourDataBase", MODE_PRIVATE, null);
                                                    ourDataBase.execSQL("INSERT INTO Categories (Name,IconId)VALUES('" + newCategoryName + "',-1)");//todo if user put image than insert real iconId
                                                    ourDataBase.close();
                                                    popupWindow.dismiss();


                                                }

                                            }


            );

        }
        if(id==R.id.menuAddRecipe)
        {
            Intent intent = new Intent(this,RecipeFormActivity.class);
            startActivity(intent);
        }
        if(id==R.id.menuReturnToMainPage)
        {
            startActivity(new Intent(this,MainActivity.class));
        }


        return super.onOptionsItemSelected(item);

    }
}
