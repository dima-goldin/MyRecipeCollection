package com.example.lenovo.myrecipecollection;

import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toolbar;

import com.example.lenovo.myrecipecollection.ourUtilities.ScreenUtils;

import java.io.File;


public class MainActivity extends ActionBarActivity {

    private Intent intent;
 //   private android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
       //getWindow().setBackgroundDrawableResource(R.drawable.cupcakebackground1new);
        getWindow().setBackgroundDrawableResource(R.color.accentColor);

       // toolbar= (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
    //    setSupportActionBar(toolbar);

        loadDataBase();

    }

    private void loadDataBase() {
       // deleteDatabase("ourDataBase");
        if(dataBaseExists((ContextWrapper) getApplicationContext(),"ourDataBase"))
        {
            return;
        }

        SQLiteDatabase ourDataBase=openOrCreateDatabase("ourDataBase",MODE_PRIVATE,null);

        ourDataBase.execSQL(
                            "CREATE TABLE Categories (Name text not null primary key,CategoryFather text, IconId integer);");
        ourDataBase.execSQL(
                            "CREATE TABLE Recipes (Name text not null primary key,Instructions text,CategoryFather text, IconId integer);");
        ourDataBase.execSQL(
                            "CREATE TABLE Ingredients (ID integer not null primary key autoincrement,Name text not null,Amount double, Unit integer, RecipeName text not null);");
        int chickenPicId=R.drawable.chicken;
        ourDataBase.execSQL(
                "INSERT INTO Categories (Name,IconId) VALUES('בשרים',"+chickenPicId+")"
        );
        int dessertPic=R.drawable.cupcakebackground2;
        ourDataBase.execSQL(
                "INSERT INTO Categories (Name,IconId) VALUES('קינוחים',"+dessertPic+")"
        );
        int cupCakePic=R.drawable.cupcake1;
        //TODO debug
        String instruction1="לערבב בכלי הרבה אהבה עם סוכר"+"\n"+"לחמם את התנור לחום גבוה"+"\n"+"להגיש חם";
        ourDataBase.execSQL(
                "INSERT INTO Recipes (Name,Instructions,IconId) VALUES('עוגה','"+instruction1+"',"+cupCakePic+")"
        );
        ourDataBase.execSQL(
                "INSERT INTO Recipes (Name,IconId,CategoryFather) VALUES('עוגת גבינה',"+cupCakePic+",'קינוחים')"
        );
        ourDataBase.execSQL(
                "INSERT INTO Ingredients (Name,Amount,Unit,RecipeName)VALUES('בננות',"+"'30'"+","+"1"+","+"'עוגה'"+")"
        );
        ourDataBase.execSQL(
                "INSERT INTO Ingredients (Name,Amount,Unit,RecipeName)VALUES('סוכר',"+"'100'"+","+"5"+","+"'עוגה'"+")"
        );
        ourDataBase.close();

    }
    private static boolean dataBaseExists(ContextWrapper context,String dbName)
    {
        File dbFile= context.getDatabasePath(dbName);
        return dbFile.exists();
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
            popupWindow.showAsDropDown(findViewById(R.id.headlineHebrew));
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

    public void openMainRecipeCategories(View view)
    {
       Intent intent = new Intent(this,mainRecipeCategories.class);
       startActivity(intent);

    }

    public void openRecipeFormActivity(View view)
    {
        Intent intent = new Intent(this,RecipeFormActivity.class);
        startActivity(intent);

    }

}
