package com.grant.gymclimbtracker.gym_server;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.grant.gymclimbtracker.climb_contract.ClimbContract;
import com.grant.gymclimbtracker.climb_contract.ServerDbHelper;


public class ClimbProvider extends ContentProvider {
    private ServerDbHelper mHelper;

    public ClimbProvider() {
    }

    // used for the UriMacher
    private static final int CLIMB_LIST = 1;
    private static final int CLIMB_ID = 2;

    // create a UriMatcher object
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(ClimbContract.AUTHORITY, ClimbContract.Climbs.TABLE_NAME, CLIMB_LIST);
        URI_MATCHER.addURI(ClimbContract.AUTHORITY, ClimbContract.Climbs.TABLE_NAME + "/#", CLIMB_ID);
    }

    @Override
    public String getType(Uri uri) {
        switch(URI_MATCHER.match(uri)) {
            case CLIMB_LIST:
                return ClimbContract.Climbs.CONTENT_TYPE;
            case CLIMB_ID:
                return ClimbContract.Climbs.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI:" + uri);
        }
    }

    @Override
    public boolean onCreate() {
        mHelper = new ServerDbHelper(getContext());
        return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if(URI_MATCHER.match(uri) != CLIMB_LIST){
            throw new IllegalArgumentException("Unsupported insertion URI: " + uri);
        }

        SQLiteDatabase db = mHelper.getWritableDatabase();
        long id = db.insert(ClimbContract.Climbs.TABLE_NAME,
                null,
                values);
        return getUriForId(id, uri);

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        /**
         * Return Cursor from SQLiteDatabase query()
         *  if query matches no rows, return empty cursor (getCount = 0)
         *  return null if internal error occured
         */

        SQLiteDatabase db = mHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        switch(URI_MATCHER.match(uri)) {
            case CLIMB_LIST:
                builder.setTables(ClimbContract.Climbs.TABLE_NAME);
                if(TextUtils.isEmpty(sortOrder)) {
                    sortOrder = ClimbContract.Climbs.SORT_ORDER_DEFAULT;
                }
                break;
            case CLIMB_ID:
                builder.setTables(ClimbContract.Climbs.TABLE_NAME);
                builder.appendWhere(ClimbContract.Climbs._ID + " = " +
                    uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unsupported query URI:" + uri);
        }

        Cursor cursor = builder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);

        // make sure potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int updateCount = 0;
        switch(URI_MATCHER.match(uri)) {
            case CLIMB_LIST:
                updateCount = db.update(ClimbContract.Climbs.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case CLIMB_ID:
                String idStr = uri.getLastPathSegment();
                String where = ClimbContract.Climbs._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                updateCount = db.update(ClimbContract.Climbs.TABLE_NAME,
                        values,
                        where,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported update URI: " + uri);
        }
        if (updateCount > 0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return updateCount;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int deleteCount = 0;
        switch(URI_MATCHER.match(uri)) {
            case CLIMB_LIST:
                deleteCount = db.delete(ClimbContract.Climbs.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CLIMB_ID:
                String idStr = uri.getLastPathSegment();
                String where = ClimbContract.Climbs._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                deleteCount = db.delete(ClimbContract.Climbs.TABLE_NAME,
                        where,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported delete URI: " + uri);
        }
        if (deleteCount > 0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return deleteCount;
    }

    private Uri getUriForId(long id, Uri uri)  {
        if (id > 0) {
            // notify all listeners of changes and return uri
            Uri climbUri = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(climbUri,null);
            return climbUri;
        }
        // something went wrong
        throw new SQLException("Problem while inserting into uri: " + uri);
    }


}
