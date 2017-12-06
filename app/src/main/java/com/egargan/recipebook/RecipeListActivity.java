package com.egargan.recipebook;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.egargan.recipebook.provider.Contract;
import com.egargan.recipebook.provider.Contract.Recipe;

/**
 * Activity for listing the contents of the app's recipe database.
 *
 * Does most of the heavy-lifting in terms of database interaction,
 * therefore implements LoaderCallbacks<Cursor> for background data gets.
 */
public class RecipeListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ID_LOADER = 0;

    // Cursor adapter for recipe list view, assigned after db query completes
    private SimpleCursorAdapter rcpListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        // Create loader for querying recipe content provider, will invoke 'onCreateLoader()'
        getLoaderManager().initLoader(ID_LOADER, null, this);
    }

    @Override
    protected void onDestroy() {

        // Don't need to worry about closing db, content provider handles it for us.

        getLoaderManager().destroyLoader(ID_LOADER);

        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // Called when loader is created, i.e. through initLoader(...)
        // Construct query for loader to perform - here, just get all recipe data.

        return new CursorLoader(this, Contract.RCP_TABLE_URI,
                null, null,
                null, null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        rcpListAdapter.swapCursor(null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // Called when loader finished getting data from provider.
        // Will only ever return a cursor to entire recipe table, as above.

        ListView rcpListView = findViewById(R.id.listView_recipe);

        rcpListAdapter = new SimpleCursorAdapter(this,

                android.R.layout.simple_list_item_2, // Layout of individual list items

                data, // Cursor object returned by loader

                // Projection on database
                new String[] {Recipe.NAME_COL_TITLE, Recipe.NAME_COL_INSTRUCTIONS},

                // Get IDs of layout's text fields
                new int[] { android.R.id.text1, android.R.id.text2 },

                0);

        rcpListView.setAdapter(rcpListAdapter);
        rcpListView.setOnItemClickListener(recipeItemListener);

        rcpListAdapter.swapCursor(data);

        // Don't close cursor! Must persist for cursor adapter
    }

    // Listener attached to each listview element
    private final AdapterView.OnItemClickListener recipeItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            // Cursor gets 'id' from data set's _ID column, thf. can be safely used to reference
            launchRecipeViewActivity( Uri.parse(Contract.RCP_TABLE_URI + "/" + id ));
        }
    };


    /** Handler for 'add' button. Launches RecipeViewActivity with no URI */
    public void onClickAdd(View btn) {
        launchRecipeViewActivity(null);
    }

    /** Launches RecipeEditActivity to show the recipe with the given ID.
     * @param rcpUri Uri pointing to recipe in database. */
    private void launchRecipeViewActivity(Uri rcpUri) {

        Intent i = new Intent(this, RecipeEditActivity.class);

        // If called by a listview element click, then pass its recipe URI to view/edit activity
        if (rcpUri != null) i.setData(rcpUri);

        startActivity(i);
    }



}
