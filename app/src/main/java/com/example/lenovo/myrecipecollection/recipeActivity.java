package com.example.lenovo.myrecipecollection;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

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
        getMenuInflater().inflate(R.menu.menu_recipe, menu);
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
}
