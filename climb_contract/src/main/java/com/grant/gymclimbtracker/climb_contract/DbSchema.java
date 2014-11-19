package com.grant.gymclimbtracker.climb_contract;

/**
 * Created by Grant on 11/1/2014.
 */
public class DbSchema {
    /**
     * A helper interface which defines constants for work with the DB
     */
    public static final String DATABASE_NAME = "climbs.db";
    public static final int DATABASE_VERSION = 2;

    private static final int typeSize = ClimbContract.climbType.values().length;
    private static final int ropeGradeSize = ClimbContract.ropeGrade.values().length;
    private static final int boulderGradeSize = ClimbContract.boulderGrade.values().length;


    public static final String CREATE_TBL_CLIMBS =
            "CREATE TABLE " + ClimbContract.Climbs.TABLE_NAME + "(" +
                    ClimbContract.Climbs._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
                    ClimbContract.Climbs.GYM + " TEXT NOT NULL,\n" +
                    ClimbContract.Climbs.AREA + " TEXT NOT NULL,\n" +
                    ClimbContract.Climbs.TYPE + " INTEGER NOT NULL,\n" +
                    ClimbContract.Climbs.GRADE + " INTEGER NOT NULL,\n" +
                    ClimbContract.Climbs.DATE_SET + " TEXT NOT NULL,\n" +
                    ClimbContract.Climbs.COLOR_PRIMARY + " INTEGER NOT NULL, \n" +
                    ClimbContract.Climbs.COLOR_SECONDARY + " INTEGER, \n" +
                    ClimbContract.Climbs.SETTER + " TEXT,\n" +
                    ClimbContract.Climbs.NUM_CLIMBED + " INTEGER,\n" +
                    ClimbContract.Climbs.DATE_REMOVED + " DATE,\n" +
                    "CONSTRAINT chk_Type CHECK (" + getCheckBoundsString(ClimbContract.Climbs.TYPE, 0, typeSize-1) + "), \n" +
                    "CONSTRAINT chk_Grade CHECK (((" + getCheckEqualsString(ClimbContract.Climbs.TYPE, ClimbContract.climbType.lead.ordinal()) + " OR " +
                                                    getCheckEqualsString(ClimbContract.Climbs.TYPE, ClimbContract.climbType.toprope.ordinal()) + ") AND " +
                                                    getCheckBoundsString(ClimbContract.Climbs.GRADE, 0, ropeGradeSize-1) + ") OR (" +
                                                    getCheckEqualsString(ClimbContract.Climbs.TYPE, ClimbContract.climbType.boulder.ordinal()) + " AND " +
                                                    getCheckBoundsString(ClimbContract.Climbs.GRADE, 0, boulderGradeSize-1) + ")) \n" +

                    ")";

    public static final String DROP_TBL_CLIMBS =
            "DROP TABLE IF EXISTS " + ClimbContract.Climbs.TABLE_NAME;

    public static String getCheckBoundsString(String field, int minInclusive, int maxInclusive) {
        /**
         * return the string to check bounds of given field
         */
        return field + ">= " + minInclusive + " AND " + field + "<=" + maxInclusive;
    }

    public static String getCheckEqualsString(String field, int value) {
        return field + "=" + value;
    }
}
