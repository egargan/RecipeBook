package com.egargan.recipebook.RecipeDB;

import android.provider.BaseColumns;

/**
 * Contract class for recipe database.
 * This class specifies the layout of the recipe database's schema.
 */
public class Contract {

    public static final String AUTHORITY = "com.egargan.recipebook.RecipeDB.RecipeProvider";

    public static final String RCP_TABLE_URI = "content://" + AUTHORITY + RecipeTable.TABLE_NAME;

    /** Inner class defining recipe table contents.
     *  Not necessary, but makes for more extensible code if e.g. more tables were to be added. */
    public static class RecipeTable implements BaseColumns {

        // Implementing BaseColumns gives us a primary key field '_ID'.

        public static final String TABLE_NAME = "recipes";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_INSTRUCTIONS = "instructions";
    }

    /** Private constructor to prevent class being instantiated. */
    private Contract() {}

}
