package com.egargan.recipebook.provider;

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

/**
 * Content provider to interface with recipe database described in DbHelper.
 * @see DbHelper
 */
public class RecipeProvider extends ContentProvider {

    private DbHelper dbHelper;

    private static final UriMatcher matcher;

    private static final int RECIPES = 1;
    private static final int RECIPE_ID = 2;

    //
    static {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(Contract.AUTHORITY, Contract.Recipe.NAME_TABLE, RECIPES);
        matcher.addURI(Contract.AUTHORITY, Contract.Recipe.NAME_TABLE + "/#", RECIPE_ID);
    }

    @Override
    public boolean onCreate() {

        // Invoked when application starts - is not called by us!
        dbHelper = new DbHelper(getContext());

        return true; // TODO: check if db succesfully loaded, and reflect in return value.
    }

    @Nullable @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteQueryBuilder qbuilder = new SQLiteQueryBuilder();

        // If individual record requested, apply selection to query builder
        switch (matcher.match(uri)) {

            case RECIPE_ID:
                qbuilder.appendWhere(Contract.Recipe._ID + "="
                        + uri.getLastPathSegment());

                // Case falls through, so recipe table is queried in either case

            case RECIPES :
                qbuilder.setTables(Contract.Recipe.NAME_TABLE);
                break;

            default :
                Log.e("prov","Table at uri not found.");
                return null;
        }

        Cursor cursor = qbuilder.query(dbHelper.getReadableDatabase(), projection, selection,
                selectionArgs, null, null, null);

        // Necessary for content observers to properly catch change notifications of data at uri.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable @Override
    public String getType(@NonNull Uri uri) {

        // Returns MIME string denoting db item types, either an entire table (dir),
        // or a single record (item)

        if (uri.getLastPathSegment() == null) {
            return "vnd.android.cursor.dir/RecipeProvder.data.text";
        } else {
            return "vnd.android.cursor.item/RecipeProvder.data.text";
        }

    }

    @Nullable @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        long id;
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (matcher.match(uri)) {

            case RECIPES :
                id = db.insert(Contract.Recipe.NAME_TABLE, null, values);
                break;

            default:
                Log.e("prov","Table at uri not found.");
                return null;
        }

        // Alert content observers that are observing the data at 'uri' that it has changed (?)
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

                deletions = db.delete(Contract.Recipe.NAME_TABLE,
                        selection, selectionArgs);
                break;

            case RECIPE_ID :

                // If non-null selection given, append 'AND' so we can tag on ID selection string
                if (selection != null) {
                    selection += " AND ";
                }
                selection += Contract.Recipe._ID + "=" + uri.getLastPathSegment();

                deletions = db.delete(Contract.Recipe.NAME_TABLE,
                        selection, selectionArgs);
                break;

            default :
                Log.e("prov","Table at uri not found.");
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return deletions;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Will hold number of rows updated, as returned by db.update(...)
        int updates = 0;

        switch (matcher.match(uri)) {

            case RECIPES :

                updates = db.update(Contract.Recipe.NAME_TABLE,
                        values, selection, selectionArgs);
                break;

            case RECIPE_ID :

                if (selection != null) {
                    selection += " AND ";
                }
                selection += Contract.Recipe._ID + "=" + uri.getLastPathSegment();

                updates = db.update(Contract.Recipe.NAME_TABLE,
                        values,
                        selection,
                        selectionArgs);

                break;

            default :
                Log.e("prov","Table at uri not found.");
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return updates;
    }

}
