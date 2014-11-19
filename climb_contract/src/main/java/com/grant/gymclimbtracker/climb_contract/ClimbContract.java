package com.grant.gymclimbtracker.climb_contract;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Grant on 11/1/2014.
 */
public class ClimbContract {
    /**
     * The authority of climb provider
     */
    public static final String AUTHORITY = "com.grant.gymclimbtracker.gym_server.ClimbProvider";

    /**
     * content URI for top-level climb authority
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
/*
    public static void setAUTHORITY(String authority) {
        AUTHORITY = authority;
        CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    }
*/
    /**
     * Constants for climb objects
     */
    public static final class Climbs implements BaseColumns {
        /**
         * Uri table path.  This is also the sql database table name
         */
        public static final String TABLE_NAME = "climbs";

        /**
         * fields for climbs, if the climb is still up, dateRemoved will be NULL
         */
        public static final String GYM = "gym";
        public static final String AREA = "area";
        public static final String TYPE = "type";
        public static final String GRADE = "grade";
        public static final String DATE_SET = "dateSet";
        public static final String COLOR_PRIMARY = "colorPrimary";
        public static final String COLOR_SECONDARY = "colorSecondary";
        public static final String SETTER = "setter";
        public static final String NUM_CLIMBED = "numClimbed";
        public static final String DATE_REMOVED = "dateRemoved";

        /**
         * The content URI for this table
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ClimbContract.CONTENT_URI, TABLE_NAME);

        /**
         * The MIME type of a directory of climbs
         */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.com.grant.gymclimbtracker.gym_server.climbs";

        /**
         * The MIME type of a single climb
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.com.grant.gymclimbtracker.gym_server.climbs";

        /**
         * A projection of all columns in the table
         */
        public static final String[] PROJECTION_ALL = {_ID, GYM, AREA, TYPE, GRADE, DATE_SET, COLOR_PRIMARY, COLOR_SECONDARY, SETTER, NUM_CLIMBED, DATE_REMOVED};

        /**
         * The default sort order for queries containing GRADE fields
          */
        public static final String SORT_ORDER_DEFAULT = GRADE + " ASC";


    }


    public static interface CommonClimbColumns extends BaseColumns{



    }

    /**
     * Enum definitions for type
     */
    public enum climbType{
        // type of climb
        boulder("Bouldering"), toprope("Toprope"), lead("Lead");

        private String label;

        private climbType(String label){
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public enum ropeGrade{
        // top rope grade
        yds4("5.4"),
        yds5("5.5"),
        yds6("5.6"),
        yds7("5.7"),
        yds8("5.8"),
        yds9("5.9"),
        yds10a("5.10a"),
        yds10b("5.10b"),
        yds10c("5.10c"),
        yds10d("5.10d"),
        yds11a("5.11a"),
        yds11b("5.11b"),
        yds11c("5.11c"),
        yds11d("5.11d"),
        yds12a("5.12a"),
        yds12b("5.12b"),
        yds12c("5.12c"),
        yds12d("5.12d"),
        yds13a("5.13a"),
        yds13b("5.13b"),
        yds13c("5.13c"),
        yds13d("5.13d");

        private String label;
        private ropeGrade(String label){
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public enum boulderGrade{
        // top boulder grade
        v0("v0"),
        v1("v1"),
        v2("v2"),
        v3("v3"),
        v4("v4"),
        v5("v5"),
        v6("v6"),
        v7("v7"),
        v8("v8"),
        v9("v9"),
        v10("v10"),
        v11("v11"),
        v12("v12"),
        v13("v13"),
        v14("v14"),
        v15("v15");

        private String label;
        private boulderGrade(String label){
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

/*
    public static void createIfNonExistent(Context context){
        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = context.getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if(packageInfo.packageName.equals("com.grant.gymclimbtracker.gym_client.ClimbProvider") ||
                    packageInfo.packageName.equals("com.grant.gymclimbtracker.climber_client.ClimbProvider")) {
                // climbprovider already exists, so use it as the content provider URI
                setAUTHORITY(packageInfo.packageName);
                return;
            }
        }
        pm.setComponentEnabledSetting(new ComponentName(context,  context.getPackageName() + ".ClimbProvider"),
                PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
                PackageManager.DONT_KILL_APP);

    }
 */
}
