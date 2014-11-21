package com.grant.gymclimbtracker.gym_client;

import com.grant.gymclimbtracker.climb_contract.ClimbContract;

import static com.grant.gymclimbtracker.climb_contract.DbSchema.getCheckBoundsString;
import static com.grant.gymclimbtracker.climb_contract.DbSchema.getCheckEqualsString;

/**
 * Created by Grant on 11/1/2014.
 */
public class GymLocalDbSchema {
    /**
     * A helper interface which defines constants for work with the DB
     */


    public static final String DATABASE_NAME = "localGymDb";

    public static final String TABLE_COLORS = "myclimbs";
    public static final String COLUMN_COLOR = "color";
    public static final String[] ALL_COLORS_COLUMNS = {COLUMN_COLOR};

    public static final int DATABASE_VERSION = 1;

    public static final String CREATE_TBL_COLORS =
            "CREATE TABLE " + TABLE_COLORS + "(" +
                    COLUMN_COLOR + " INTEGER PRIMARY KEY \n" +
                    ")";

    public static final String DROP_TBL_COLORS =
            "DROP TABLE IF EXISTS " + TABLE_COLORS;

}
