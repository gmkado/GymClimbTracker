package com.grant.gymclimbtracker.climber_client;

import com.grant.gymclimbtracker.climb_contract.ClimbContract;

import static com.grant.gymclimbtracker.climb_contract.DbSchema.getCheckBoundsString;
import static com.grant.gymclimbtracker.climb_contract.DbSchema.getCheckEqualsString;

/**
 * Created by Grant on 11/1/2014.
 */
public class ClimberLocalDbSchema {
    /**
     * A helper interface which defines constants for work with the DB
     */

    private static final int typeSize = ClimbContract.climbType.values().length;
    private static final int ropeGradeSize = ClimbContract.ropeGrade.values().length;
    private static final int boulderGradeSize = ClimbContract.boulderGrade.values().length;

    public static final String DATABASE_NAME = "localClimbDb";

    public static final String TABLE_MYCLIMBS = "myclimbs";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PROJECT = "project";
    public static final String COLUMN_ATTEMPTS = "attempts";
    public static final String COLUMN_SENT = "sends";
    public static final String[] ALL_MYCLIMBS_COLUMNS = {COLUMN_ID, COLUMN_PROJECT, COLUMN_ATTEMPTS, COLUMN_SENT};

    public static final String TABLE_MYSTATS = "mystats";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_GRADE = "grade";
    public static final String COLUMN_TOTAL = "total"; // how many of each grade you climbed
    public static final String[] ALL_MYSTATS_COLUMNS = {COLUMN_TYPE,COLUMN_GRADE, COLUMN_TOTAL};
    public static final int DATABASE_VERSION = 2;

    public static final String CREATE_TBL_MYCLIMBS =
            "CREATE TABLE " + TABLE_MYCLIMBS + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY, \n" +
                    COLUMN_PROJECT + " BOOLEAN NOT NULL,\n" +
                    COLUMN_ATTEMPTS + " INTEGER NOT NULL,\n" +
                    COLUMN_SENT + " BOOLEAN NOT NULL\n" +
                    ")";

    public static final String CREATE_TBL_MYSTATS =
            "CREATE TABLE " + TABLE_MYSTATS + "(" +
                    COLUMN_TYPE + " INTEGER NOT NULL, \n" +
                    COLUMN_GRADE + " INTEGER NOT NULL,\n" +
                    COLUMN_TOTAL + " INTEGER NOT NULL,\n" +
                    "CONSTRAINT pk_TypeGradeID PRIMARY KEY ("+COLUMN_TYPE+","+COLUMN_GRADE+"),\n" +
                    "CONSTRAINT chk_Type CHECK (" + getCheckBoundsString(COLUMN_TYPE, 0, typeSize - 1) + "), \n" +
                    "CONSTRAINT chk_Grade CHECK (((" + getCheckEqualsString(COLUMN_TYPE, ClimbContract.climbType.lead.ordinal()) + " OR " +
                    getCheckEqualsString(COLUMN_TYPE, ClimbContract.climbType.toprope.ordinal()) + ") AND " +
                    getCheckBoundsString(COLUMN_GRADE, 0, ropeGradeSize-1) + ") OR (" +
                    getCheckEqualsString(COLUMN_TYPE, ClimbContract.climbType.boulder.ordinal()) + " AND " +
                    getCheckBoundsString(COLUMN_GRADE, 0, boulderGradeSize-1) + ")) \n" +
                    ")";


    public static final String DROP_TBL_MYCLIMBS =
            "DROP TABLE IF EXISTS " + TABLE_MYCLIMBS;

    public static final String DROP_TBL_MYSTATS =
            "DROP TABLE IF EXISTS " + TABLE_MYSTATS;


}
