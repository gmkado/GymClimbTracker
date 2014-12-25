package com.grant.gymclimbtracker.climber_client;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Grant on 10/26/2014.
 */
public class ClimberLocalDbSource {
    private static final String TAG = "LocalDbSource";
    private SQLiteDatabase database;
    private LocalDbHelper helper;
    private Context mContext;

    public ClimberLocalDbSource(Context context) {
        mContext = context;
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
        Cursor cursor = database.query(ClimberLocalDbSchema.TABLE_MYCLIMBS,
                new String[]{ClimberLocalDbSchema.COLUMN_PROJECT},
                ClimberLocalDbSchema.COLUMN_ID + "=" + _id,
                null, null, null, null);
        boolean hasItems = cursor.moveToFirst();
        // if not add it with value true
        if (!hasItems) {
            insertMyClimb(_id, true, 0, false);
        } else {
            // otherwise if it is a project, remove it.  if it isn't, add it
            boolean addProject = !(cursor.getInt(cursor.getColumnIndex(ClimberLocalDbSchema.COLUMN_PROJECT))>0);

            ContentValues values = new ContentValues();
            values.put(ClimberLocalDbSchema.COLUMN_PROJECT, addProject);
            database.update(ClimberLocalDbSchema.TABLE_MYCLIMBS, values,
                    ClimberLocalDbSchema.COLUMN_ID + "=" + _id, null);
        }
    }

    private void insertMyClimb(int _id, boolean project, int attempts, boolean sent) {
        ContentValues values = new ContentValues();
        values.put(ClimberLocalDbSchema.COLUMN_ID, _id);
        values.put(ClimberLocalDbSchema.COLUMN_PROJECT, project);
        values.put(ClimberLocalDbSchema.COLUMN_ATTEMPTS, attempts);
        values.put(ClimberLocalDbSchema.COLUMN_SENT, sent);

        database.insert(ClimberLocalDbSchema.TABLE_MYCLIMBS, null, values);
    }

    public void incrementAttemptCount(int _id) {
        // check if already added
        Cursor cursor = database.query(ClimberLocalDbSchema.TABLE_MYCLIMBS,
                ClimberLocalDbSchema.ALL_MYCLIMBS_COLUMNS,
                ClimberLocalDbSchema.COLUMN_ID + "=" + _id,
                null, null, null, null);

        boolean hasItem = cursor.moveToFirst();
        // if not add it, if it is increment numattempts
        if (!hasItem) {
            insertMyClimb(_id, false, 1, false);
        }else {
            int numAttempts = cursor.getInt(cursor.getColumnIndex(ClimberLocalDbSchema.COLUMN_ATTEMPTS));

            ContentValues values = new ContentValues();
            values.put(ClimberLocalDbSchema.COLUMN_ATTEMPTS, numAttempts + 1);
            database.update(ClimberLocalDbSchema.TABLE_MYCLIMBS, values,
                    ClimberLocalDbSchema.COLUMN_ID + "=" + _id, null);
        }
    }

    public void addRemoveSent(int _id, int type, int grade) {
        // check if already in table
        Cursor cursor = database.query(ClimberLocalDbSchema.TABLE_MYCLIMBS,
                ClimberLocalDbSchema.ALL_MYCLIMBS_COLUMNS,
                ClimberLocalDbSchema.COLUMN_ID + "=" + _id,
                null, null, null, null);
        boolean hasItem = cursor.moveToFirst();
        boolean addSent = true;
        if (!hasItem) {
            // climb hasn't been added yet, so add sent
            insertMyClimb(_id, false, 0, true);
        } else {
            //  toggle sent
            addSent = !(cursor.getInt(cursor.getColumnIndex(ClimberLocalDbSchema.COLUMN_SENT))>0);

            ContentValues values = new ContentValues();
            values.put(ClimberLocalDbSchema.COLUMN_SENT, addSent);

            // if the climb was sent, remove from project list
            if(addSent == true) {
                values.put(ClimberLocalDbSchema.COLUMN_PROJECT, false);
                // congratulate climber
                Toast toast = Toast.makeText(mContext,"Nice Job!", Toast.LENGTH_SHORT);
                toast.show();
            }
            database.update(ClimberLocalDbSchema.TABLE_MYCLIMBS, values,
                    ClimberLocalDbSchema.COLUMN_ID + "=" + _id, null);
            values.put(ClimberLocalDbSchema.COLUMN_SENT, addSent);
        }

        // update stats accordingly
        updateStats(type, grade, addSent);

    }

    public void updateStats(int type, int grade, boolean increment) {
        // if increment = true, increment the grade/type combo, if false then decrement

        // first check if the type, grade combo is in the table
        ContentValues values = new ContentValues();
        Cursor cursor = database.query(ClimberLocalDbSchema.TABLE_MYSTATS,
                ClimberLocalDbSchema.ALL_MYSTATS_COLUMNS,
                ClimberLocalDbSchema.COLUMN_TYPE + "=?  AND " + ClimberLocalDbSchema.COLUMN_GRADE + "=?",
                new String[] {String.valueOf(type), String.valueOf(grade)},null, null, null);

        boolean hasItem = cursor.moveToFirst();
        if(!hasItem){
            if(increment){
                values.put(ClimberLocalDbSchema.COLUMN_TOTAL, 1);
                values.put(ClimberLocalDbSchema.COLUMN_TYPE, type);
                values.put(ClimberLocalDbSchema.COLUMN_GRADE, grade);
                database.insert(ClimberLocalDbSchema.TABLE_MYSTATS,
                        null,
                        values);
            }else{
                // can't decrement, since never been added
                Log.e(TAG, "Cannot decrement stat, not in database");
                return;
            }
        }
        else{
            int total = cursor.getInt(cursor.getColumnIndex(ClimberLocalDbSchema.COLUMN_TOTAL));
            if(increment){
                values.put(ClimberLocalDbSchema.COLUMN_TOTAL, total+1);
            }else{
                if(total == 0) {
                    Log.e(TAG, "Cannot decrement stat, stat = 0");
                    return;
                } else {
                    values.put(ClimberLocalDbSchema.COLUMN_TOTAL, total - 1);
                }
            }
            database.update(ClimberLocalDbSchema.TABLE_MYSTATS,
                    values,
                    ClimberLocalDbSchema.COLUMN_TYPE + "=?  AND " + ClimberLocalDbSchema.COLUMN_GRADE + "=?",
                    new String[] {String.valueOf(type), String.valueOf(grade)});
        }
    }

    public boolean isProject(int id) {
        // check if climb given by id is a project
        Cursor cursor = database.query(ClimberLocalDbSchema.TABLE_MYCLIMBS,
                new String[]{ClimberLocalDbSchema.COLUMN_PROJECT},
                ClimberLocalDbSchema.COLUMN_ID + " = " + id,
                null, null, null, null);
        if(cursor.getCount() == 0) {
            return false;
        }else{
            cursor.moveToFirst();
            return cursor.getInt(cursor.getColumnIndex(ClimberLocalDbSchema.COLUMN_PROJECT))>0;
        }
    }

    public boolean isSent(int id) {
        // check if climb given by id is a project
        Cursor cursor = database.query(ClimberLocalDbSchema.TABLE_MYCLIMBS,
                new String[]{ClimberLocalDbSchema.COLUMN_SENT},
                ClimberLocalDbSchema.COLUMN_ID + " = " + id,
                null, null, null, null);
        if(cursor.getCount() == 0) {
            return false;
        }else{
            cursor.moveToFirst();
            return cursor.getInt(cursor.getColumnIndex(ClimberLocalDbSchema.COLUMN_SENT))>0;
        }
    }

    public String getProjectIds() {
        Cursor cursor = database.query(ClimberLocalDbSchema.TABLE_MYCLIMBS,
                new String[] {ClimberLocalDbSchema.COLUMN_ID},
                ClimberLocalDbSchema.COLUMN_PROJECT + " > 0",
                null, null, null, null);
        List<String> array = new ArrayList<String>();
        while(cursor.moveToNext()){
            array.add(cursor.getString(cursor.getColumnIndex(ClimberLocalDbSchema.COLUMN_ID)));
        }
        return array.toString().replace('[', '(').replace(']',')');  // change [1,2,3...] to (1,2,3...)
    }

    public String getSentIds() {
        Cursor cursor = database.query(ClimberLocalDbSchema.TABLE_MYCLIMBS,
                new String[] {ClimberLocalDbSchema.COLUMN_ID},
                ClimberLocalDbSchema.COLUMN_SENT + " > 0",
                null, null, null, null);
        List<String> array = new ArrayList<String>();
        while(cursor.moveToNext()){
            array.add(cursor.getString(cursor.getColumnIndex(ClimberLocalDbSchema.COLUMN_ID)));
        }
        return array.toString().replace('[', '(').replace(']',')');  // change [1,2,3...] to (1,2,3...)
    }


    class LocalDbHelper extends SQLiteOpenHelper {

        public LocalDbHelper(Context context) {
            super(context, ClimberLocalDbSchema.DATABASE_NAME, null, ClimberLocalDbSchema.DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(ClimberLocalDbSchema.CREATE_TBL_MYCLIMBS);
            db.execSQL(ClimberLocalDbSchema.CREATE_TBL_MYSTATS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            /**
             * for now, erase old data and create a new one
             * TODO: change this so we don't drop all the data
             */
            db.execSQL(ClimberLocalDbSchema.DROP_TBL_MYCLIMBS);
            db.execSQL(ClimberLocalDbSchema.DROP_TBL_MYSTATS);
            onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            /**
             * for now, erase old data and create a new one
             * TODO: change this so we don't drop all the data
             */
            db.execSQL(ClimberLocalDbSchema.DROP_TBL_MYCLIMBS);
            db.execSQL(ClimberLocalDbSchema.DROP_TBL_MYSTATS);
            onCreate(db);
        }
    }
}
