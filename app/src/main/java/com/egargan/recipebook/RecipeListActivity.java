package com.egargan.recipebook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RecipeListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
    }


    @Override
    protected void onDestroy() {

        // close db here, however we're going to access it
        super.onDestroy();
    }
}
