package com.egargan.recipebook.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.egargan.recipebook.provider.Contract.Recipe;


/**
 * Helper class for creating and maintaining recipe database.
 * Will create database as per the local 'Contract' class.
 * @see Contract
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "rcpDb";
    private static final int DB_VERSION = 1;

    private static final String SQL_CREATE_RECIPES =
            "CREATE TABLE " + Recipe.NAME_TABLE + " (" +
            Recipe._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Recipe.NAME_COL_TITLE + " TEXT," +
            Recipe.NAME_COL_INSTRUCTIONS + " TEXT" +
            ");";

    private static final String SQL_DROP_RECIPES =
            "DROP TABLE IF EXISTS " + Recipe.NAME_TABLE;

    /** Class constructor. Simply calls SQLiteOpenHelper's constructor. */
    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Called when provider attempts to open the database and SQLite reports it does not exist.
        db.execSQL(SQL_CREATE_RECIPES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_RECIPES);
        onCreate(db);
    }
}
