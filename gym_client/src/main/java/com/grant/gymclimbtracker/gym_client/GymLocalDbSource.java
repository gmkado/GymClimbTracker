package com.grant.gymclimbtracker.gym_client;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.grant.gymclimbtracker.climb_contract.DbSchema;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Grant on 10/26/2014.
 */
public class GymLocalDbSource {
    private static final String TAG = "LocalDbSource";
    private SQLiteDatabase database;
    private LocalDbHelper helper;

    public GymLocalDbSource(Context context) {
        helper = new LocalDbHelper(context);
    }

    public void open() throws SQLException {
        database = helper.getWritableDatabase();
    }

    public void close() {
        helper.close();
    }


    public void insertColor(int color) {
        ContentValues values = new ContentValues();
        values.put(GymLocalDbSchema.COLUMN_COLOR, color);

        database.insert(GymLocalDbSchema.TABLE_COLORS, null, values);
    }

    public ArrayList<Integer> getColors() {
        // return list of colors
        Cursor cursor = database.query(GymLocalDbSchema.TABLE_COLORS,
                new String[] {GymLocalDbSchema.COLUMN_COLOR},
                null,null, null, null, null);
        ArrayList<Integer> array = new ArrayList<Integer>();

        while(cursor.moveToNext()){
            array.add(cursor.getInt(cursor.getColumnIndex(GymLocalDbSchema.COLUMN_COLOR)));
        }
        return array;
    }

    public void replaceColors(ArrayList<Integer> colorList) {
        // delete the entire table
        database.delete(GymLocalDbSchema.TABLE_COLORS, null, null);

        for (int color:colorList) {
            insertColor(color);
        }
    }

    class LocalDbHelper extends SQLiteOpenHelper {

        public LocalDbHelper(Context context) {
            super(context, GymLocalDbSchema.DATABASE_NAME, null, GymLocalDbSchema.DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(GymLocalDbSchema.CREATE_TBL_COLORS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            /**
             * for now, erase old data and create a new one
             * TODO: change this so we don't drop all the data
             */
            db.execSQL(GymLocalDbSchema.DROP_TBL_COLORS);
            onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            /**
             * for now, erase old data and create a new one
             * TODO: change this so we don't drop all the data
             */
            db.execSQL(GymLocalDbSchema.DROP_TBL_COLORS);
            onCreate(db);
        }
    }
}
