package com.grant.gymclimbtracker.climber_client;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Grant on 10/26/2014.
 */
public class LocalDbSource {
    private static final String TAG = "LocalDbSource";
    private SQLiteDatabase database;
    private LocalDbHelper helper;

    public LocalDbSource(Context context) {
        helper = new LocalDbHelper(context);
    }

    public void open() throws SQLException {
        database = helper.getWritableDatabase();
    }

    public void close() {
        helper.close();
    }

    public void addRemoveProject(int _id) {
        // check if already added
        Cursor cursor = database.query(LocalDbSchema.TABLE_MYCLIMBS,
                new String[]{LocalDbSchema.COLUMN_PROJECT},
                LocalDbSchema.COLUMN_ID + "=" + _id,
                null, null, null, null);
        boolean hasItems = cursor.moveToFirst();
        // if not add it with value true
        if (!hasItems) {
            insertMyClimb(_id, true, 0, false);
        } else {
            // otherwise if it is a project, remove it.  if it isn't, add it
            boolean addProject = !(cursor.getInt(cursor.getColumnIndex(LocalDbSchema.COLUMN_PROJECT))>0);

            ContentValues values = new ContentValues();
            values.put(LocalDbSchema.COLUMN_PROJECT, addProject);
            database.update(LocalDbSchema.TABLE_MYCLIMBS, values,
                    LocalDbSchema.COLUMN_ID + "=" + _id, null);
        }
    }

    private void insertMyClimb(int _id, boolean project, int attempts, boolean sent) {
        ContentValues values = new ContentValues();
        values.put(LocalDbSchema.COLUMN_ID, _id);
        values.put(LocalDbSchema.COLUMN_PROJECT, project);
        values.put(LocalDbSchema.COLUMN_ATTEMPTS, attempts);
        values.put(LocalDbSchema.COLUMN_SENT, sent);

        database.insert(LocalDbSchema.TABLE_MYCLIMBS, null, values);
    }

    public void incrementAttemptCount(int _id) {
        // check if already added
        Cursor cursor = database.query(LocalDbSchema.TABLE_MYCLIMBS,
                LocalDbSchema.ALL_MYCLIMBS_COLUMNS,
                LocalDbSchema.COLUMN_ID + "=" + _id,
                null, null, null, null);

        boolean hasItem = cursor.moveToFirst();
        // if not add it, if it is increment numattempts
        if (!hasItem) {
            insertMyClimb(_id, false, 1, false);
        }else {
            int numAttempts = cursor.getInt(cursor.getColumnIndex(LocalDbSchema.COLUMN_ATTEMPTS));

            ContentValues values = new ContentValues();
            values.put(LocalDbSchema.COLUMN_ATTEMPTS, numAttempts + 1);
            database.update(LocalDbSchema.TABLE_MYCLIMBS, values,
                    LocalDbSchema.COLUMN_ID + "=" + _id, null);
        }
    }

    public void addRemoveSent(int _id, int type, int grade) {
        // check if already in table
        Cursor cursor = database.query(LocalDbSchema.TABLE_MYCLIMBS,
                LocalDbSchema.ALL_MYCLIMBS_COLUMNS,
                LocalDbSchema.COLUMN_ID + "=" + _id,
                null, null, null, null);
        boolean hasItem = cursor.moveToFirst();
        boolean addSent = true;
        if (!hasItem) {
            // climb hasn't been added yet, so add sent
            insertMyClimb(_id, false, 0, true);
        } else {
            //  toggle sent
            addSent = !(cursor.getInt(cursor.getColumnIndex(LocalDbSchema.COLUMN_SENT))>0);

            ContentValues values = new ContentValues();
            values.put(LocalDbSchema.COLUMN_SENT, addSent);
            database.update(LocalDbSchema.TABLE_MYCLIMBS, values,
                    LocalDbSchema.COLUMN_ID + "=" + _id, null);
            values.put(LocalDbSchema.COLUMN_SENT, addSent);
        }

        // update stats accordingly
        updateStats(type, grade, addSent);

    }

    public void updateStats(int type, int grade, boolean increment) {
        // if increment = true, increment the grade/type combo, if false then decrement

        // first check if the type, grade combo is in the table
        ContentValues values = new ContentValues();
        Cursor cursor = database.query(LocalDbSchema.TABLE_MYSTATS,
                LocalDbSchema.ALL_MYSTATS_COLUMNS,
                LocalDbSchema.COLUMN_TYPE + "=?  AND " + LocalDbSchema.COLUMN_GRADE + "=?",
                new String[] {String.valueOf(type), String.valueOf(grade)},null, null, null);

        boolean hasItem = cursor.moveToFirst();
        if(!hasItem){
            if(increment){
                values.put(LocalDbSchema.COLUMN_TOTAL, 1);
                values.put(LocalDbSchema.COLUMN_TYPE, type);
                values.put(LocalDbSchema.COLUMN_GRADE, grade);
                database.insert(LocalDbSchema.TABLE_MYSTATS,
                        null,
                        values);
            }else{
                // can't decrement, since never been added
                Log.e(TAG, "Cannot decrement stat, not in database");
                return;
            }
        }
        else{
            int total = cursor.getInt(cursor.getColumnIndex(LocalDbSchema.COLUMN_TOTAL));
            if(increment){
                values.put(LocalDbSchema.COLUMN_TOTAL, total+1);
            }else{
                if(total == 0) {
                    Log.e(TAG, "Cannot decrement stat, stat = 0");
                    return;
                } else {
                    values.put(LocalDbSchema.COLUMN_TOTAL, total - 1);
                }
            }
            database.update(LocalDbSchema.TABLE_MYSTATS,
                    values,
                    LocalDbSchema.COLUMN_TYPE + "=?  AND " + LocalDbSchema.COLUMN_GRADE + "=?",
                    new String[] {String.valueOf(type), String.valueOf(grade)});
        }
    }

    public boolean isProject(int id) {
        // check if climb given by id is a project
        Cursor cursor = database.query(LocalDbSchema.TABLE_MYCLIMBS,
                new String[]{LocalDbSchema.COLUMN_PROJECT},
                LocalDbSchema.COLUMN_ID + " = " + id,
                null, null, null, null);
        if(cursor.getCount() == 0) {
            return false;
        }else{
            cursor.moveToFirst();
            return cursor.getInt(cursor.getColumnIndex(LocalDbSchema.COLUMN_PROJECT))>0;
        }
    }

    public boolean isSent(int id) {
        // check if climb given by id is a project
        Cursor cursor = database.query(LocalDbSchema.TABLE_MYCLIMBS,
                new String[]{LocalDbSchema.COLUMN_SENT},
                LocalDbSchema.COLUMN_ID + " = " + id,
                null, null, null, null);
        if(cursor.getCount() == 0) {
            return false;
        }else{
            cursor.moveToFirst();
            return cursor.getInt(cursor.getColumnIndex(LocalDbSchema.COLUMN_SENT))>0;
        }
    }

    public String getProjectIds() {
        Cursor cursor = database.query(LocalDbSchema.TABLE_MYCLIMBS,
                new String[] {LocalDbSchema.COLUMN_ID},
                LocalDbSchema.COLUMN_PROJECT + " > 0",
                null, null, null, null);
        List<String> array = new ArrayList<String>();
        while(cursor.moveToNext()){
            array.add(cursor.getString(cursor.getColumnIndex(LocalDbSchema.COLUMN_ID)));
        }
        return array.toString().replace('[', '(').replace(']',')');  // change [1,2,3...] to (1,2,3...)
    }

    public String getSentIds() {
        Cursor cursor = database.query(LocalDbSchema.TABLE_MYCLIMBS,
                new String[] {LocalDbSchema.COLUMN_ID},
                LocalDbSchema.COLUMN_SENT + " > 0",
                null, null, null, null);
        List<String> array = new ArrayList<String>();
        while(cursor.moveToNext()){
            array.add(cursor.getString(cursor.getColumnIndex(LocalDbSchema.COLUMN_ID)));
        }
        return array.toString().replace('[', '(').replace(']',')');  // change [1,2,3...] to (1,2,3...)
    }


    class LocalDbHelper extends SQLiteOpenHelper {

        public LocalDbHelper(Context context) {
            super(context, LocalDbSchema.DATABASE_NAME, null, LocalDbSchema.DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(LocalDbSchema.CREATE_TBL_MYCLIMBS);
            db.execSQL(LocalDbSchema.CREATE_TBL_MYSTATS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            /**
             * for now, erase old data and create a new one
             * TODO: change this so we don't drop all the data
             */
            db.execSQL(LocalDbSchema.DROP_TBL_MYCLIMBS);
            db.execSQL(LocalDbSchema.DROP_TBL_MYSTATS);
            onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            /**
             * for now, erase old data and create a new one
             * TODO: change this so we don't drop all the data
             */
            db.execSQL(LocalDbSchema.DROP_TBL_MYCLIMBS);
            db.execSQL(LocalDbSchema.DROP_TBL_MYSTATS);
            onCreate(db);
        }
    }
}
