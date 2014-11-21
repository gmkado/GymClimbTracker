package com.grant.gymclimbtracker.gym_client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import java.util.List;
import java.lang.Integer;

/**
 * Created by Grant on 11/19/2014.
 */
public class ColorSpinnerAdapter<T extends Integer> extends ArrayAdapter implements SpinnerAdapter {
    private List<Integer> colors;
    private LayoutInflater mInflater;

    public ColorSpinnerAdapter(Context context, List<Integer> colors)
    {
        super(context, android.R.layout.simple_list_item_1, colors);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.colors = colors;
    }

    @Override
    public int getCount() {
        return colors.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);

    }



    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        //super.getDropDownView(position, convertView,parent);

        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if(rowView == null) {
            // get new instance of row layout view
            rowView = mInflater.inflate(android.R.layout.simple_list_item_1,null);
        }

        rowView.setBackgroundColor(colors.get(position));

        return rowView;
    }
}
