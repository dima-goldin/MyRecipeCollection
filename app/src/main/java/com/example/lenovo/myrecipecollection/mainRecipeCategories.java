package com.example.lenovo.myrecipecollection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.internal.app.ToolbarActionBar;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;


public class mainRecipeCategories extends ActionBarActivity {

    private Point p=new Point(10,10);
    private Intent intent;

    private ArrayAdapter<Category> adapter;
    private ListView list;

    private String parentCategory=null;

  //  private android.support.v7.widget.Toolbar toolbar;

    private List<Category> mainCategoriesList= new ArrayList<Category>();

    private int saveFlag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recipe_categories_new);
//todo make save button invisable
        //populateMainCategoriesList();
        populateMainListView();

    //    toolbar= (android.support.v7.widget.Toolbar) findViewById(R.id.categorybar);
      //  setSupportActionBar(toolbar);
        populateCategoriesList(null);
        if(getCallingActivity()!=null)
        {
            saveFlag=1;
            //todo make save button visible
        }
//        longClickOnCategoriesList();
        ListView listView=(ListView)findViewById(R.id.mainCategoriesListView);
        registerForContextMenu(listView);

    }

//    private void longClickOnCategoriesList() {
//        ListView listView=(ListView)findViewById(R.id.mainCategoriesListView);
//        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                //TODO
//                return false;
//            }
//        });
//    }

    private void populateMainListView() {
        adapter=new MyMainCategoryListAdapter();
         list= (ListView) findViewById(R.id.mainCategoriesListView);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               TextView descriptionView = (TextView)view.findViewById(R.id.recipeOrCategoryId);
                String description=descriptionView.getText().toString();
                TextView nameView = (TextView)view.findViewById(R.id.categoryName);
                String name=nameView.getText().toString();
                if(description.equals("קטגוריה"))
                {
                    populateCategoriesList(name);

                }
                else if (description.equals("מתכון"))
                {
                    intent = new Intent(getApplicationContext(),recipeActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("key", name);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }


            }
        });

    }
    private class MyMainCategoryListAdapter extends ArrayAdapter<Category>{
        public MyMainCategoryListAdapter() {
            super(mainRecipeCategories.this, R.layout.categorylayout,mainCategoriesList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View itemView=convertView;
            if(itemView==null){
                itemView=getLayoutInflater().inflate(R.layout.categorylayout,parent,false);
            }
            //Find the category to work with
            Category currentCategory = mainCategoriesList.get(position);
            //Fill the view
            ImageView imageView = (ImageView) itemView.findViewById(R.id.CategoryIcon);
            if(currentCategory.getIconID()==-1)
            {
                imageView.setVisibility(View.INVISIBLE);
            }
            if(currentCategory.getIconID()!=-1)
            {
                imageView.setImageResource(currentCategory.getIconID());
            }

            //make
            TextView makeText=(TextView)itemView.findViewById(R.id.categoryName);
            makeText.setText(currentCategory.getName());
            TextView descriptionText=(TextView)itemView.findViewById(R.id.recipeOrCategoryId);
            descriptionText.setText(currentCategory.getDescription());
            return itemView;
        }
    }


    private void addNewCategory(String categoryName,int iconId)
    {
        Category newCategory= new Category(categoryName,iconId,parentCategory,"קטגוריה");
        //add this category to database
        populateCategoriesList(parentCategory);
    }

    private void populateCategoriesList(String parent)
    {
        //get from database all recipes and categories that parent is parent
        //clear adapter
        // in a loop add all those to the mainCategoriesList
        SQLiteDatabase ourDataBase=openOrCreateDatabase("ourDataBase",MODE_PRIVATE,null);
        Cursor resultSet1;
        Cursor resultSet2;
        if(parent==null)
        {
            resultSet1 =ourDataBase.rawQuery("SELECT Name,CategoryFather,IconID FROM Categories WHERE CategoryFather IS NULL", null);
            resultSet2 =ourDataBase.rawQuery("SELECT Name,CategoryFather,IconID FROM Recipes WHERE CategoryFather IS NULL",null);

        }
        else{
            resultSet1=ourDataBase.rawQuery("SELECT Name,CategoryFather,IconID FROM Categories WHERE CategoryFather='"+parent+"'",null);
            resultSet2=ourDataBase.rawQuery("SELECT Name,CategoryFather,IconID FROM Recipes WHERE CategoryFather='"+parent+"'",null);

        }
        mainCategoriesList.clear();
        while(resultSet1.moveToNext())
        {
            String name=resultSet1.getString(0);
            String father=resultSet1.getString(1);
            int iconId=resultSet1.getInt(2);
            mainCategoriesList.add(new Category(name,iconId,father,"קטגוריה"));

        }

        while(resultSet2.moveToNext())
        {
            String name=resultSet2.getString(0);
            String father=resultSet2.getString(1);
            int iconId=resultSet2.getInt(2);
            mainCategoriesList.add(new Category(name,iconId,father,"מתכון"));

        }
        adapter.notifyDataSetChanged();
        ourDataBase.close();
        parentCategory=parent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_recipe_categories, menu);
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
        if(id==R.id.navigateBack){
            startActivity(new Intent(this,MainActivity.class));
        }
        if(id==R.id.addCategory){
        //opens popup to write name and pic
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
            popupWindow.showAsDropDown(findViewById(R.id.addCategory));
           //popupWindow.showAtLocation(this.g, Gravity.CENTER,0,0);

            Button submitButton=(Button)popupView.findViewById(R.id.popUpSubmitCategoryButton);
            submitButton.setOnClickListener(new Button.OnClickListener(){
               @Override
                   public void onClick(View v){
                   EditText nameText= (EditText)popupView.findViewById(R.id.editPopUpCategoryName);
                   String newCategoryName=nameText.getText().toString();
                   SQLiteDatabase ourDataBase=openOrCreateDatabase("ourDataBase",MODE_PRIVATE,null);
                   if(parentCategory==null)
                   {
                       ourDataBase.execSQL("INSERT INTO Categories (Name,IconId)VALUES('"+newCategoryName+"',-1)");//todo if user put image than insert real iconId
                   }
                   else
                   {
                       ourDataBase.execSQL("INSERT INTO Categories (Name,CategoryFather,IconId)VALUES('"+newCategoryName+"','"+parentCategory+"',-1)");//todo if user put image than insert real iconId
                   }
                   ourDataBase.close();
                   popupWindow.dismiss();
                   populateCategoriesList(parentCategory);

               }
                 }

            );

        }
        if(id==R.id.saveRecipe)
        {
            if(saveFlag!=1)
            {
                return false;
            }
            saveFlag=0;
            Intent returnIntent=new Intent();
            returnIntent.putExtra("categoryFather",parentCategory);
            setResult(RESULT_OK,returnIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu,View v,ContextMenu.ContextMenuInfo menuInfo){

        menu.setHeaderTitle("אפשרויות");
        String[] options = {"הסר","חזור"};

        for(String option: options)
        {
            menu.add(option);
        }

    }
    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int selectedIndex = info.position;

        if(item.getTitle().equals("הסר")){

            Category selectedCategory=mainCategoriesList.get(selectedIndex);
            final String desc=selectedCategory.getDescription();
            final String name=selectedCategory.getName();

            SQLiteDatabase ourDataBase=openOrCreateDatabase("ourDataBase",MODE_PRIVATE,null);
            Cursor cursor= ourDataBase.rawQuery("SELECT Name FROM Categories WHERE CategoryFather='"+name+"'",null);
            if(cursor.getCount()!=0)
            {

                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("שגיאה!");
                alert.setMessage("לא ניתן למחוק קטגוריה המכילה תתי קטגוריות");
                cursor.close();
                ourDataBase.close();
                alert.show();
                return false;
            }
            cursor.close();


            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("אזהרה!");
            alert.setMessage("אתם עומדים למחוק מתכון או קטגוריה יחד עם כל תכולותו. להמשיך במחיקה?");
            alert.setPositiveButton("המשך",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //erase
                    if(desc=="מתכון")
                    {
                        SQLiteDatabase ourDataBase=openOrCreateDatabase("ourDataBase",MODE_PRIVATE,null);
                        ourDataBase.execSQL("DELETE FROM Recipes WHERE Name='"+name+"'");
                        ourDataBase.execSQL(("DELETE FROM Ingredients WHERE RecipeName='"+name+"'"));

                        ourDataBase.close();
                    }
                    else if(desc=="קטגוריה")
                    {
                        SQLiteDatabase ourDataBase=openOrCreateDatabase("ourDataBase",MODE_PRIVATE,null);

                        Cursor recipeCursorSet=ourDataBase.rawQuery("SELECT Name FROM Recipes WHERE CategoryFather='"+name+"'",null);
                        while(recipeCursorSet.moveToNext())
                        {
                            String currentRecipeName=recipeCursorSet.getString(0);
                            ourDataBase.execSQL("DELETE FROM Ingredients WHERE RecipeName='"+currentRecipeName+"'");
                        }
                        ourDataBase.execSQL("DELETE FROM Recipes WHERE CategoryFather='"+name+"'");
                        ourDataBase.execSQL("DELETE FROM Categories WHERE Name='"+name+"'");
                        recipeCursorSet.close();
                        ourDataBase.close();
                    }
                    mainCategoriesList.remove(selectedIndex);
                    adapter.notifyDataSetChanged();
                }
            });
            alert.setNegativeButton("בטל",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });

            alert.show();



        }

        return true;
    }


}




//todo func for popup when clicking on add a new category button
//it should open a popup that asks for a name and picture of the category
//and will have a submit button

//todo func for submit button
// it will get the name and pic from the user
//and send it to addNewCategory func