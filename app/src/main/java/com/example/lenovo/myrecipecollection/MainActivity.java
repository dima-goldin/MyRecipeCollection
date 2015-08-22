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

import com.example.lenovo.myrecipecollection.ourUtilities.MySQLiteHelper;
import com.example.lenovo.myrecipecollection.ourUtilities.ScreenUtils;

import java.io.File;


public class MainActivity extends ActionBarActivity {

    private Intent intent;
    MySQLiteHelper db;
 //   private android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        db = new MySQLiteHelper(this);
        getWindow().setBackgroundDrawableResource(R.color.accentColor);

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

            Button submitButton=(Button)popupView.findViewById(R.id.popUpSubmitCategoryButton);
            submitButton.setOnClickListener(new Button.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    EditText nameText = (EditText) popupView.findViewById(R.id.editPopUpCategoryName);
                                                    String newCategoryName = nameText.getText().toString();
                                                    db.insertCategory(newCategoryName,null,-1);
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
        intent.putExtra("edit","no");
        startActivity(intent);

    }

}
