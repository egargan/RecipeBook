package com.egargan.recipebook.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract class for recipe database.
 * This class specifies the layout of the recipe database's schema.
 */
public class Contract {

    // Must match authority declared in manifest!
    public static final String AUTHORITY = RecipeProvider.class.getCanonicalName();

    public static final Uri RCP_TABLE_URI = Uri.parse("content://" + AUTHORITY + "/" + Recipe.NAME_TABLE);

    // MIME types of table + individual records
    public static final String MIME_TABLE = "vnd.android.cursor.dir/RecipeProvder.data.text";
    public static final String MIME_RECIPE = "vnd.android.cursor.item/RecipeProvder.data.text";

    /** Inner class defining recipe table contents.
     *  Not necessary, but makes for more extensible code if e.g. more tables were to be added. */
    public static class Recipe implements BaseColumns {

        // Implementing BaseColumns gives us a primary key field '_ID'.

        public static final String NAME_TABLE = "recipes";
        public static final String NAME_COL_TITLE = "title";
        public static final String NAME_COL_INSTRUCTIONS = "instructions";

        public static final int INDEX_COL_ID = 0;
        public static final int INDEX_COL_TITLE = 1;
        public static final int INDEX_COL_INSTRUCTIONS = 2;
    }

    /** Private constructor to prevent class being instantiated. */
    private Contract() {}

}
