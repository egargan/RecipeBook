package com.egargan.recipebook.RecipeDB;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class RecipeProvider extends ContentProvider {

    private DbHelper dbHelper;

    private static final UriMatcher matcher;

    private static final int RECIPES = 1;
    private static final int RECIPE_ID = 2;

    static {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(Contract.AUTHORITY, Contract.RecipeTable.TABLE_NAME, RECIPES);
        matcher.addURI(Contract.AUTHORITY,Contract.RecipeTable.TABLE_NAME + "/#", RECIPE_ID);
    }

    @Override
    public boolean onCreate() {

        dbHelper = new DbHelper(getContext());

        return false; // set to true when done probs?
    }

    @Nullable @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteQueryBuilder qbuilder = new SQLiteQueryBuilder();

        // If individual record requested, apply selection to query builder
        switch (matcher.match(uri)) {

            case RECIPE_ID:
                qbuilder.appendWhere(Contract.RecipeTable._ID + "="
                        + uri.getLastPathSegment());

                // Case falls through, so recipe table is queried in either case

            case RECIPES :
                qbuilder.setTables(Contract.RecipeTable.TABLE_NAME);
                break;

            default :
                Log.e("prov","Table at uri not found.");
                return null;
        }

        Cursor cursor = qbuilder.query(dbHelper.getReadableDatabase(), projection, selection,
                selectionArgs, null, null, sortOrder);

        // Necessary for content observers to properly catch change notifications of data at uri.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        long id;
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (matcher.match(uri)) {

            case RECIPES :
                id = db.insert(Contract.RecipeTable.TABLE_NAME, null, values);
                break;

            default:
                Log.e("prov","Table at uri not found.");
                return null;
        }

        // Alert content observers to changes
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int deletions = 0;

        switch (matcher.match(uri)) {

            case RECIPES :

                deletions = db.delete(Contract.RecipeTable.TABLE_NAME,
                        selection, selectionArgs);
                break;

            case RECIPE_ID :

                // If non-null selection given, append 'AND' so we can tag on ID selection string
                if (selection != null) {
                    selection += " AND ";
                }
                selection += Contract.RecipeTable._ID + "=" + uri.getLastPathSegment();

                deletions = db.delete(Contract.RecipeTable.TABLE_NAME,
                        selection, selectionArgs);

            default :
                Log.e("prov","Table at uri not found.");
        }



        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Will hold number of rows updated, as returned by db.update(...)
        int updates = 0;

        switch (matcher.match(uri)) {

            case RECIPES :

                updates = db.update(Contract.RecipeTable.TABLE_NAME,
                        values, selection, selectionArgs);
                break;

            case RECIPE_ID :

                if (selection != null) {
                    selection += " AND ";
                }
                selection += Contract.RecipeTable._ID + "=" + uri.getLastPathSegment();

                updates = db.update(Contract.RecipeTable.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);

                break;

            default :
                Log.e("prov","Table at uri not found.");
        }

        return updates;
    }

}
