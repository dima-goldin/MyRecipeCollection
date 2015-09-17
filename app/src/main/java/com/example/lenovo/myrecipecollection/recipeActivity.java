package com.example.lenovo.myrecipecollection;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.lenovo.myrecipecollection.ourUtilities.BitmapUtils;
import com.example.lenovo.myrecipecollection.ourUtilities.MySQLiteHelper;

import java.util.ArrayList;
import java.util.List;


public class recipeActivity extends ActionBarActivity {
    MySQLiteHelper db;
    TextView instructionsView;
    ImageButton iconView;
    TextView ingsView;
    Boolean enlarged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        db = new MySQLiteHelper(this);
        instructionsView=(TextView)findViewById(R.id.showInstructions);
        iconView=(ImageButton)findViewById(R.id.showIcon);
        ingsView=(TextView)findViewById(R.id.showIngs);
        String recipeName=getIntent().getExtras().getString("key");
        showRecipe(recipeName);

    }

    private void showRecipe(String recipeName) {
        Recipe recipe = db.getRecipe(recipeName);
        fillRecipe(recipeName, recipe.getInstructions(), recipe.getPicture(), recipe.getIngredientList(), recipe.getParent());
    }

    private void fillRecipe(String name,String instructions,Bitmap picture,List<Ingredient> ingredientList,String father)
    {
        TextView nameView= (TextView) findViewById(R.id.showRecipeNameTitle);
        nameView.setText(name);

        instructionsView.setText(instructions);
        iconView.setImageBitmap(picture);
        String ingsString=new String();
        for(Ingredient ing:ingredientList)
        {
            ingsString=ingsString+ing.toString()+"\n";
        }
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
                                                    Bitmap picture = BitmapUtils.drawableToBitmap(getResources().getDrawable(R.drawable.notavaliable));
                                                    db.insertCategory(newCategoryName,null,picture);
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

    public void enlargePic(View view)
    {
        if(enlarged)
        {
            //make smaller
            iconView.setMaxHeight(100);
            iconView.setMinimumHeight(100);
            iconView.setMaxWidth(100);
            iconView.setMinimumWidth(100);
            iconView.getLayoutParams().height -= 412;
            iconView.getLayoutParams().width -= 412;
            enlarged = false;
        }
        else
        {
            //make larger
            iconView.setMaxHeight(512);
            iconView.setMinimumHeight(512);
            iconView.setMaxWidth(512);
            iconView.setMinimumWidth(512);
            iconView.getLayoutParams().height += 412;
            iconView.getLayoutParams().width += 412;
            enlarged = true;
        }
    }
}
