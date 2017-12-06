package com.egargan.recipebook;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.egargan.recipebook.provider.Contract;

/**
 * Activity for displaying and editing recipes.
 *
 * Includes two edit-text fields for a recipe's title and instructions,
 * and buttons for entering edit mode, for saving/canceling changes, and recipe deletion.
 */
public class RecipeEditActivity extends AppCompatActivity {

    private Uri recipeUri;

    private boolean newRecipe;
    private boolean editMode;

    private TextView txtTitle;
    private TextView txtInstructions;

    private Button delBtn;
    private Button cancelBtn;
    private Button editBtn; // Changes from 'edit' to 'save' while edit mode is active.

    // Animations for edit controls - both buttons appear from behind the edit/save button.
    Animation delBtnAnim;
    Animation cancelBtnAnim;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_edit);

        txtTitle = findViewById(R.id.txtTitle);
        txtInstructions = findViewById(R.id.txtInstructions);

        delBtn = findViewById(R.id.btnDelete);
        cancelBtn = findViewById(R.id.btnCancel);
        editBtn = findViewById(R.id.btnEdit);

        // Construct animations for cancel + delete buttons' appearance
        delBtnAnim = AnimationUtils.loadAnimation(this, R.anim.slide_left_del);
        cancelBtnAnim = AnimationUtils.loadAnimation(this, R.anim.slide_left_cancel);

        delBtnAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        cancelBtnAnim.setInterpolator(new AccelerateDecelerateInterpolator());

        if (getIntent().getData() != null) {

            newRecipe = false;

            // If we've been given data, attempt to load it as recipe.
            loadRecipe(getIntent().getData());
            makeEditable(false);

        } else {

            newRecipe = true;
            makeEditable(true); // If no recipe given, assume 'new' recipe context
        }
    }

    /** Attempts to load recipe at given URI and display title and instructions.
     * @param uri URI pointing to recipe. */
    private void loadRecipe(Uri uri) {

        if (!getContentResolver().getType(uri).equals(Contract.MIME_RECIPE)) {
            Log.i("rcpViewActivity", "Given URI is not recipe.");
            return;
        }

        recipeUri = uri;

        // Recipe exists, so load it
        Cursor c = getContentResolver().query(uri,
                null,
                null,
                null,
                null
                );

        c.moveToFirst(); // Move cursor to actually point to returned record

        txtTitle.setText(c.getString(Contract.Recipe.INDEX_COL_TITLE));
        txtInstructions.setText(c.getString(Contract.Recipe.INDEX_COL_INSTRUCTIONS));

        c.close();
    }

    /** Changes various view components to allow user to edit the viewed recipe, or if already in
     * 'edit' mode, hide these edit controls and simply display recipe.
     *
     * @param editable True if activity to be set to 'edit' mode, or false to simply view recipe. */
    private void makeEditable(boolean editable) {

        int visibility = editable ? View.VISIBLE : View.GONE;

        // Set visibility of edit controls
        cancelBtn.setVisibility(visibility);

        // If new recipe, then it cannot be deleted, thf. don't show at all
        delBtn.setVisibility(newRecipe ? View.GONE : visibility);

        // Change edit/save button text accordingly
        editBtn.setText(editable ? R.string.btnStringSave : R.string.btnStringEdit);

        // Change text fields to un/editable
        txtTitle.setFocusable(editable);
        txtTitle.setFocusableInTouchMode(editable);

        txtInstructions.setFocusable(editable);
        txtInstructions.setFocusableInTouchMode(editable);

        // Get InputMethodManager to control keyboard state
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (editable) {

            txtTitle.requestFocus();

            // Show keyboard, and set to edit text title
            imm.showSoftInput(txtTitle, InputMethodManager.HIDE_IMPLICIT_ONLY);

        } else {

            // Hide keyboard when exiting 'edit' mode
            imm.hideSoftInputFromWindow(txtTitle.getWindowToken(), 0);

            // Set focus to dummy view to prevent further editing of text view
            findViewById(R.id.div).requestFocus();
        }

        editMode = editable;
    }

    /** Gets activities 'title' and 'instructions' text fields and either inserts new recipe to db,
     * or updates existing recipe with these values. */
    private void saveChanges() {

        String givenTitle = txtTitle.getText().toString();
        String givenInstructions = txtInstructions.getText().toString();

        // If either input strings are empty, give default values.
        if (givenTitle.isEmpty()) givenTitle = "Untitled Recipe";
        if (givenInstructions.isEmpty()) givenInstructions = "No instructions given.";

        ContentValues vals = new ContentValues();
        vals.put(Contract.Recipe.NAME_COL_TITLE, givenTitle);
        vals.put(Contract.Recipe.NAME_COL_INSTRUCTIONS, givenInstructions);

        if (newRecipe) {

            // Then insert new recipe into db
            recipeUri = getContentResolver().insert(Contract.RCP_TABLE_URI, vals);
            newRecipe = false;

        } else {

            // Recipe exists, just update it
            getContentResolver().update(recipeUri, vals, null, null);
        }

    }

    //  --- Button onClick methods ---  //


    /** Handler for edit/save button. Button acts as both edit mode trigger and save button.
     *
     * If simply viewing recipe, then enter 'edit' mode.
     * @see this#makeEditable(boolean)
     *
     * If editing recipe, then make changes to / insert recipe title + instructions to db.
     * @see this#saveChanges() */
    public void onClickEditSave(View btn) {

        if (!editMode) {

            // If in 'view' mode, then enter 'edit' mode and return
            makeEditable(true);

            // Start animations
            delBtn.startAnimation(delBtnAnim);
            cancelBtn.startAnimation(cancelBtnAnim);

        } else {

            // Else commit changes to db and 'view' new recipe.
            saveChanges();
            makeEditable(false);

            // Cancel any animations if incomplete
            delBtnAnim.reset();
            cancelBtnAnim.reset();
        }
    }

    /** Deletes the recipe being viewed from the database. */
    public void onClickDelete(View btn) {

        getContentResolver().delete(recipeUri, null, null);
        finish();
    }

    /** Handler for 'cancel' button. If recipe exists, then cancel current changes and view
     * original; if not, close activity. */
    public void onClickCancel(View btn) {

        if (newRecipe) {
            // Ignore any inputted text and close activity
            finish();
        } else {

            // Ignore changes to recipe and display original recipe title + instructions.
            loadRecipe(recipeUri);
        }
        makeEditable(false);
    }

}
