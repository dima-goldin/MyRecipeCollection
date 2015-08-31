package com.example.lenovo.myrecipecollection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.lenovo.myrecipecollection.ourUtilities.BitmapUtils;
import com.example.lenovo.myrecipecollection.ourUtilities.MySQLiteHelper;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;


public class mainRecipeCategories extends ActionBarActivity {

    public static final String WHERE_TO_SAVE = "איפה תרצו לשמור את המתכון?";
    private MySQLiteHelper db;
    private Point p=new Point(10,10);
    private Intent intent;

    private ArrayAdapter<Category> adapter;
    private ListView list;

    private String parentCategory=null;
    Deque<String> categoryStack;
    TextView textViewTitle;

  //  private android.support.v7.widget.Toolbar toolbar;

    private List<Category> mainCategoriesList= new ArrayList<Category>();

    private int saveFlag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recipe_categories_new);
//todo make save button invisable
        db = new MySQLiteHelper(this);
        categoryStack  = new ArrayDeque<String>();
        textViewTitle = (TextView) findViewById(R.id.myRecipesTitle);
        populateMainListView();


    //    toolbar= (android.support.v7.widget.Toolbar) findViewById(R.id.categorybar);
      //  setSupportActionBar(toolbar);
        populateCategoriesList(null);
        if(getCallingActivity()!=null)
        {
            saveFlag=1;

        }
//        longClickOnCategoriesList();
        ListView listView=(ListView)findViewById(R.id.mainCategoriesListView);
        registerForContextMenu(listView);

        Intent intent = getIntent();
        String fromSavingRecipe = intent.getStringExtra(RecipeFormActivity.SAVEING_RECIPE);
        if(fromSavingRecipe != null)
        {
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.main_recipe_cat_parent_layout);
            Button button = new Button(this);
            button.setText("שמור");
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.setMargins(50, 50, 0, 0);
            button.setLayoutParams(params);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("categoryFather", parentCategory);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            });
            layout.addView(button);


            textViewTitle.setText(WHERE_TO_SAVE);
        }

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


    @Override
    protected void onResume() {
        super.onResume();
        //populateMainListView();
        populateCategoriesList(parentCategory);
    }

    private void populateMainListView() {
        adapter=new MyMainCategoryListAdapter();
         list= (ListView) findViewById(R.id.mainCategoriesListView);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView descriptionView = (TextView) view.findViewById(R.id.recipeOrCategoryId);
                String description = descriptionView.getText().toString();
                TextView nameView = (TextView) view.findViewById(R.id.categoryName);
                String name = nameView.getText().toString();
                if (description.equals("קטגוריה")) {
                    populateCategoriesList(name);
                    categoryStack.push(name);

                } else if (description.equals("מתכון")) {
                    intent = new Intent(getApplicationContext(), recipeActivity.class);
                    Bundle bundle = new Bundle();
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
            if(currentCategory.getPicture()==null)
            {
                imageView.setVisibility(View.INVISIBLE);
            }
            else
            {
                imageView.setImageBitmap(currentCategory.getPicture());
            }

            //make
            TextView makeText=(TextView)itemView.findViewById(R.id.categoryName);
            makeText.setText(currentCategory.getName());
            TextView descriptionText=(TextView)itemView.findViewById(R.id.recipeOrCategoryId);
            descriptionText.setText(currentCategory.getDescription());
            return itemView;
        }
    }


//    private void addNewCategory(String categoryName,Bitmap iconId)
//    {
//        Category newCategory= new Category(categoryName,iconId,parentCategory,"קטגוריה");
//        //add this category to database
//        populateCategoriesList(parentCategory);
//    }

    private void populateCategoriesList(String parent)
    {
        //get from database all recipes and categories that parent is parent
        //clear adapter
        // in a loop add all those to the mainCategoriesList


        mainCategoriesList.clear();
        ArrayList<Category> categories = db.getCategoriesByFather(parent);
        if(categories != null)
        {
            for(Category category : categories)
            {
                category.setDescription("קטגוריה");
                mainCategoriesList.add(category);
            }
        }
        ArrayList<Recipe> recipesByCategory = db.getRecipesByCategory(parent);
        if(recipesByCategory != null)
        {
            for(Recipe recipe : recipesByCategory)
            {
                String name=recipe.getName();
                String father=recipe.getParent();
                Bitmap picture=recipe.getPicture();
                mainCategoriesList.add(new Category(name,picture,father,"מתכון"));

            }
        }
        String currentCategory = parent;
        if(currentCategory == null &&textViewTitle.getText().toString().startsWith(WHERE_TO_SAVE))
        {
            textViewTitle.setText(WHERE_TO_SAVE);
        }
        else if(currentCategory == null)
        {
            textViewTitle.setText("המתכונים שלי");
        }
        else if(textViewTitle.getText().toString().startsWith(WHERE_TO_SAVE))
        {
            textViewTitle.setText(WHERE_TO_SAVE + " קטגוריה: " + currentCategory) ;
        }
        else
        {
            textViewTitle.setText("המתכונים שלי " + " קטגוריה: " + currentCategory) ;
        }
        adapter.notifyDataSetChanged();
        parentCategory=parent;

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

        if(id==R.id.menuReturnToMainPage){
            startActivity(new Intent(this,MainActivity.class));
        }
        if(id==R.id.menuAddCategory){
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
            popupWindow.showAsDropDown(findViewById(R.id.myRecipesTitle));
           //popupWindow.showAtLocation(this.g, Gravity.CENTER,0,0);

            Button submitButton=(Button)popupView.findViewById(R.id.popUpSubmitCategoryButton);
            submitButton.setOnClickListener(new Button.OnClickListener(){
               @Override
                   public void onClick(View v){
                   EditText nameText= (EditText)popupView.findViewById(R.id.editPopUpCategoryName);
                   String newCategoryName=nameText.getText().toString();

                   //TODO this is only temporary, i need to get the bitmap from gallery/camera and turn into a bitmap
                   Bitmap picture = BitmapUtils.drawableToBitmap(getResources().getDrawable(R.drawable.notavaliable));
                   if(parentCategory==null)
                   {

                       db.insertCategory(newCategoryName,null,picture);
                   }
                   else
                   {
                       db.insertCategory(newCategoryName,parentCategory,picture);
                   }
                   popupWindow.dismiss();
                   populateCategoriesList(parentCategory);

               }
                 }

            );

        }
        if(id==R.id.menuAddRecipe)
        {
            Intent intent = new Intent(this,RecipeFormActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu,View v,ContextMenu.ContextMenuInfo menuInfo){

        menu.setHeaderTitle("אפשרויות");
        String[] options = {"הסר","חזור", "ערוך"};

        for(String option: options)
        {
            menu.add(option);
        }

    }
    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int selectedIndex = info.position;
        Category selectedCategory=mainCategoriesList.get(selectedIndex);
        final String desc=selectedCategory.getDescription();
        final String name=selectedCategory.getName();
        if(item.getTitle().equals("ערוך")){
            if(desc.equals("מתכון"))
            {
                Intent editIntent = new Intent(this,RecipeFormActivity.class);
                editIntent.putExtra("edit","yes");
                editIntent.putExtra("recipeName", name);
                startActivity(editIntent);
            }

        }
        if(item.getTitle().equals("הסר")){

            ArrayList<Category> categories = db.getCategoriesByFather(name);
            if(categories != null && categories.size() != 0)
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("שגיאה!");
                alert.setMessage("לא ניתן למחוק קטגוריה המכילה תתי קטגוריות");
                alert.show();
                return false;
            }

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("אזהרה!");
            alert.setMessage("אתם עומדים למחוק מתכון או קטגוריה יחד עם כל תכולותו. להמשיך במחיקה?");
            alert.setPositiveButton("המשך", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //erase
                    if (desc.equals("מתכון")) {
                        db.deleteRecipe(name);
                        db.deleteRecipeIngredients(name);
                    } else if (desc.equals("קטגוריה")) {
                        ArrayList<Recipe> recipes = db.getRecipesByCategory(name);
                        db.deleteCategory(name);
                        if (recipes != null) {
                            for (Recipe recipe : recipes) {
                                db.deleteRecipeIngredients(recipe.getName());
                                db.deleteRecipe(recipe.getName());
                            }
                        }

                    }
                    mainCategoriesList.remove(selectedIndex);
                    adapter.notifyDataSetChanged();
                }
            });
            alert.setNegativeButton("בטל", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });

            alert.show();



        }

        return true;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(categoryStack.isEmpty())
        {
           if(saveMode())
           {
               //notify not saved
               //finish();
               super.onBackPressed();
           }
            else
           {
               finish();
               //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
               //startActivity(intent);

           }
        }
        else
        {
            categoryStack.pop();
            populateCategoriesList(categoryStack.peek());
        }

    }

    private boolean saveMode() {
        return saveFlag == 1;
    }
}




//todo func for popup when clicking on add a new category button
//it should open a popup that asks for a name and picture of the category
//and will have a submit button

//todo func for submit button
// it will get the name and pic from the user
//and send it to addNewCategory func