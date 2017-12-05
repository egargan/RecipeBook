package com.egargan.recipebook;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.egargan.recipebook.provider.Contract;

/**
 * Activity for displaying recipes:, including two text fields for a recipe's title and instructions.
 *
 */
public class RecipeEditActivity extends AppCompatActivity {

    TextView rcpTitle;
    TextView rcpInstructions;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_view);

        rcpTitle = findViewById(R.id.txtTitle);
        rcpInstructions = findViewById(R.id.txtInstructions);


        if (getIntent().getData() != null) {

            // If we've been given data, attempt to load it as recipe.
            loadRecipe(getIntent().getData());
            makeEditable(false);

        } else {

            // If no recipe given, assume 'new' recipe context
            makeEditable(true);
        }
    }

    private void loadRecipe(Uri uri) {

        if (!getContentResolver().getType(uri).equals(Contract.MIME_RECIPE)) {
            Log.i("rcpViewActivity", "Given URI is not recipe.");
            return;
        }

        // Recipe exists, so load it:
        Cursor c = getContentResolver().query(uri,
                null,
                null,
                null,
                null
                );

        c.moveToFirst();

        rcpTitle.setText(c.getString(Contract.Recipe.INDEX_COL_TITLE));
        rcpInstructions.setText(c.getString(Contract.Recipe.INDEX_COL_INSTRUCTIONS));

        c.close();
    }

    private void makeEditable(boolean editable) {



    }

}
