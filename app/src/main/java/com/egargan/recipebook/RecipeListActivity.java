package com.egargan.recipebook;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

    private SimpleCursorAdapter rcpListAdapter;
    private ListView rcpListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        // Create loader for querying recipe content provider, will invoke 'onCreateLoader()'
        getLoaderManager().initLoader(ID_LOADER, null, this);

        // TODO : put list view population into method, probs
        rcpListView = findViewById(R.id.listView_recipe);

        // TODO: make custom layout for recipes + put in here
        rcpListAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2,

                null, // Cursor object - is set when cursor loader completes query.

                // Projection on database - must match with
                new String[] {Recipe.NAME_COL_TITLE, Recipe.NAME_COL_INSTRUCTIONS},

                // Get IDs of layout's text fields
                new int[] { android.R.id.text1, android.R.id.text2},
                0);

        rcpListView.setAdapter(rcpListAdapter);
    }

    @Override
    protected void onDestroy() {

        // close db here, however we're going to access it
        super.onDestroy();
    }

    public void dosomething(View btn) {

        // eg insert
        ContentValues vals = new ContentValues();

        vals.put(Recipe.NAME_COL_TITLE, "Egg Sandwich");
        vals.put(Recipe.NAME_COL_INSTRUCTIONS, "Put fried egg in sandwich");

        getContentResolver().insert(Contract.RCP_TABLE_URI, vals);

        // eg query
        Cursor c = getContentResolver().query(Contract.RCP_TABLE_URI,
                null,
                null, null, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // Called when loader is created, i.e. through initLoader(...)
        // Construct query for loader to perform - here, just get all recipe data.

        return new CursorLoader(this, Contract.RCP_TABLE_URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // Called when loader is finished getting data from provider.
        rcpListAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        rcpListAdapter.swapCursor(null);
    }
}
