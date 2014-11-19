package com.grant.gymclimbtracker.climb_contract;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Grant on 10/26/2014.
 */
public class ServerDbHelper extends SQLiteOpenHelper{

    public ServerDbHelper(Context context) {
        super(context, DbSchema.DATABASE_NAME, null, DbSchema.DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DbSchema.CREATE_TBL_CLIMBS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /**
         * for now, erase old data and create a new one
         * TODO: change this so we don't drop all the data
         */
        db.execSQL(DbSchema.DROP_TBL_CLIMBS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /**
         * for now, erase old data and create a new one
         * TODO: change this so we don't drop all the data
         */
        db.execSQL(DbSchema.DROP_TBL_CLIMBS);
        onCreate(db);
    }
}

