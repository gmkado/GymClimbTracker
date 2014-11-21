package com.grant.gymclimbtracker.gym_client;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grant.gymclimbtracker.climb_contract.ClimbContract;


/**
 * Created by Grant on 11/6/2014.
 */
public class GymCursorAdapter extends CursorAdapter {
    private final LayoutInflater mInflater;

    public GymCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.climb_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // start binding
        final ClimbContract.climbType[] vals = ClimbContract.climbType.values();

        ClimbContract.climbType type = vals[cursor.getInt(cursor.getColumnIndex(ClimbContract.Climbs.TYPE))];
        ((TextView)view.findViewById(R.id.typeListItem)).setText(type.toString());
        switch(type){
            case lead:
            case toprope:
                ((TextView)view.findViewById(R.id.gradeListItem)).setText(ClimbContract.ropeGrade.values()[cursor.getInt(cursor.getColumnIndex(ClimbContract.Climbs.GRADE))].toString());
                break;
            case boulder:
                ((TextView)view.findViewById(R.id.gradeListItem)).setText(ClimbContract.boulderGrade.values()[cursor.getInt(cursor.getColumnIndex(ClimbContract.Climbs.GRADE))].toString());
                break;
        }

        ((TextView)view.findViewById(R.id.areaListItem)).setText(cursor.getString(cursor.getColumnIndex(ClimbContract.Climbs.AREA)));
        ((TextView)view.findViewById(R.id.dateSetListItem)).setText(cursor.getString(cursor.getColumnIndex(ClimbContract.Climbs.DATE_SET)));

//        String[] from = {ClimbContract.Climbs.GRADE, ClimbContract.Climbs.AREA, ClimbContract.Climbs.DATE_SET, ClimbContract.Climbs.GYM, ClimbContract.Climbs.TYPE};
  //      int[] to = {R.id.grade, R.id.area, R.id.dateSet, R.id.gym, R.id.type};

        /**
         * set text colors
         */
        int primaryColor = cursor.getInt(cursor.getColumnIndex(ClimbContract.Climbs.COLOR_PRIMARY));
        view.setBackgroundColor(primaryColor);

        int secondaryColor = cursor.getInt(cursor.getColumnIndex(ClimbContract.Climbs.COLOR_SECONDARY));
        int textColor;
        float[] hsv = new float[3];
        Color.colorToHSV(primaryColor,hsv);

        if(primaryColor != secondaryColor){
            textColor = secondaryColor;

        } else if(hsv[2] < 0.3) {
            // dark background, text should be white
            textColor = Color.WHITE;
        } else {
            // light background, text should be black
            textColor = Color.BLACK;
        }

        for( int i = 0; i < ((ViewGroup)view).getChildCount(); i++ )
            if( ((ViewGroup)view).getChildAt( i ) instanceof TextView )
                ((TextView)(((ViewGroup)view).getChildAt(i))).setTextColor(textColor);

    }
}
