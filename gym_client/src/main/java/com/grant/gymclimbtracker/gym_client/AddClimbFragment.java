package com.grant.gymclimbtracker.gym_client;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;


import com.grant.gymclimbtracker.climb_contract.ClimbContract;
import com.grant.gymclimbtracker.climb_contract.ShowMapFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddClimbFragment.OnAddClimbFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddClimbFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class AddClimbFragment extends Fragment
        implements Spinner.OnItemSelectedListener, Button.OnClickListener, DatePickerDialog.OnDateSetListener, ColorPickerFragment.DialogFragmentHandler{
    private static final int COLOR_PICKER_FRAGMENT = 1;
    private static final int COLOR_REMOVER_FRAGMENT = 2;

    private static final String TAG = "AddClimbFragment";
    private static final String PREFS_NAME = "gymPrefs";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //--private static final String ARG_PARAM1 = "param1";

    //--private String mParam1;

    private OnAddClimbFragmentInteractionListener mListener;
    private Spinner gradeSpinner;
    private Spinner areaSpinner;
    private String setDate;
    private Calendar mCalendar;
    private SimpleDateFormat mDateFormat;
    private ContentResolver resolver;
    private GymLocalDbSource mDbSource;
    private ArrayList<Integer> colorList;
    private ColorSpinnerAdapter colorAdapter;
    private SharedPreferences settings;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddClimbFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddClimbFragment newInstance() {
        AddClimbFragment fragment = new AddClimbFragment();
        //--Bundle args = new Bundle();
        //--args.putString(ARG_PARAM1, param1);

        //--fragment.setArguments(args);
        return fragment;
    }
    public AddClimbFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");

        //shared preferences for persistent form values
        settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated");

        // get the gym local database
        mDbSource = mListener.getDbSource();

        colorList = mDbSource.getColors();
        if(colorList.isEmpty()){
            // make sure we at least have one color
            colorList.add(Color.WHITE);
        }

        /**
         * populate the color spinners using custom color adapter
         */
        colorAdapter = new ColorSpinnerAdapter(getActivity(), colorList);
        Spinner spinner = (Spinner)getActivity().findViewById(R.id.primaryColorSpinner);
        spinner.setAdapter(colorAdapter);
        spinner.setOnItemSelectedListener(this);

        spinner = (Spinner)getActivity().findViewById(R.id.secondaryColorSpinner);
        spinner.setAdapter(colorAdapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_climb, container, false);
        Log.i(TAG, "onCreateView");

        /**
         * Set datebutton text to current date and attach listener
         */
        mCalendar = Calendar.getInstance();
        Button button = (Button)v.findViewById(R.id.dateSelectButton);
        mDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // if we have previous setDAte, use that, otherwise use current date
        setDate = settings.getString("setDate", mDateFormat.format(mCalendar.getTime()));
        button.setText(setDate);
        button.setOnClickListener(this);

        // attach listener to buttons
        button = (Button)v.findViewById(R.id.addClimbButton);
        button.setOnClickListener(this);

        button = (Button)v.findViewById(R.id.showMapButton);
        button.setOnClickListener(this);

        button = (Button)v.findViewById(R.id.colorPickerButton);
        button.setOnClickListener(this);

        button = (Button)v.findViewById(R.id.colorRemoverButton);
        button.setOnClickListener(this);

        /**
         * populate climb type Spinner
         * When this populates, it will call the onItemSelected method that will populate the other spinners
          */
        Spinner spinner = (Spinner) v.findViewById(R.id.typeSpinner);
        spinner.setAdapter(new ArrayAdapter<ClimbContract.climbType>(getActivity(), android.R.layout.simple_spinner_item, ClimbContract.climbType.values()));
        spinner.setOnItemSelectedListener(this);

        resolver = getActivity().getContentResolver();
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(TAG, "onAttach");
        try {
            mListener = (OnAddClimbFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach");
        mListener = null;
    }

    @Override
    public String toString() {
        return "Add a route";
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        switch(adapterView.getId()){
            case R.id.typeSpinner:

                // set field variables for spinners if not already set
                if(gradeSpinner == null){
                    gradeSpinner = (Spinner) getActivity().findViewById(R.id.gradeSpinner);
                }
                if(areaSpinner == null){
                    areaSpinner = (Spinner) getActivity().findViewById(R.id.areaSpinner);
                }

                // change fields according to climb type
                if(position == ClimbContract.climbType.toprope.ordinal() || position == ClimbContract.climbType.lead.ordinal()) {
                    gradeSpinner.setAdapter(new ArrayAdapter<ClimbContract.ropeGrade>(getActivity(), android.R.layout.simple_spinner_item, ClimbContract.ropeGrade.values()));
                    areaSpinner.setAdapter(ArrayAdapter.createFromResource(getActivity(), R.array.PGSV_ropeLoc, android.R.layout.simple_spinner_item));
                }else if (position == ClimbContract.climbType.boulder.ordinal()) {
                    gradeSpinner.setAdapter(new ArrayAdapter<ClimbContract.boulderGrade>(getActivity(), android.R.layout.simple_spinner_item, ClimbContract.boulderGrade.values()));
                    areaSpinner.setAdapter(ArrayAdapter.createFromResource(getActivity(), R.array.PGSV_boulderLoc, android.R.layout.simple_spinner_item));

                }

                // set area spinner to sharedpref settings
                gradeSpinner.setSelection(settings.getInt("grade",0 ));
                areaSpinner.setSelection(settings.getInt("area", 0));

                break;
            case R.id.primaryColorSpinner:
                // if we set primary color, automatically set secondary color to the same color
                ((Spinner)getActivity().findViewById(R.id.secondaryColorSpinner)).setSelection(position);
                break;


        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }



    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        month = month+1;  // index starts at 0
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DATE, day);
        setDate = mDateFormat.format(mCalendar.getTime());
        ((Button)getActivity().findViewById(R.id.dateSelectButton)).setText(setDate);
    }

    @Override
    public void onClick(View view) {
        DialogFragment fragment;
        switch(view.getId()){
            case R.id.dateSelectButton:
                fragment = DatePickerFragment.newInstance(this);
                fragment.show(getActivity().getSupportFragmentManager(), "datePicker");
                break;
            case R.id.showMapButton:
                fragment  = ShowMapFragment.newInstance();
                fragment.show(getActivity().getSupportFragmentManager(), "showMap");
                break;
            case R.id.colorPickerButton:
                fragment = ColorPickerFragment.newInstance();
                fragment.setTargetFragment(this, COLOR_PICKER_FRAGMENT);
                fragment.show(getActivity().getSupportFragmentManager(), "colorPicker");
                break;
            case R.id.colorRemoverButton:
                fragment = ColorRemoverFragment.newInstance(colorList);
                fragment.setTargetFragment(this, COLOR_REMOVER_FRAGMENT);
                fragment.show(getActivity().getSupportFragmentManager(), "colorRemover");
                break;
            case R.id.addClimbButton:
                ContentValues c = new ContentValues();
                Activity activity = getActivity();
                c.put(ClimbContract.Climbs.GYM, getString(R.string.gym_name));
                c.put(ClimbContract.Climbs.AREA, ((Spinner)activity.findViewById(R.id.areaSpinner)).getSelectedItem().toString());
                c.put(ClimbContract.Climbs.TYPE, ((Spinner)activity.findViewById(R.id.typeSpinner)).getSelectedItemPosition());
                c.put(ClimbContract.Climbs.GRADE, ((Spinner)activity.findViewById(R.id.gradeSpinner)).getSelectedItemPosition());
                c.put(ClimbContract.Climbs.DATE_SET, setDate);
                c.put(ClimbContract.Climbs.COLOR_PRIMARY, colorList.get(((Spinner) activity.findViewById(R.id.primaryColorSpinner)).getSelectedItemPosition()));
                c.put(ClimbContract.Climbs.COLOR_SECONDARY, colorList.get(((Spinner) activity.findViewById(R.id.secondaryColorSpinner)).getSelectedItemPosition()));

                resolver.insert(ClimbContract.Climbs.CONTENT_URI, c);
                break;

            default:
                throw new IllegalArgumentException("Unexpected onClick view");

        }
    }


    @Override
    public void handleDialogResult(int requestCode, int resultCode, Bundle bundle) {
        switch (requestCode){
            case COLOR_PICKER_FRAGMENT:
                if(resultCode == Activity.RESULT_OK) {
                    // add to arraylist
                    int color = bundle.getInt("color");
                    if (!colorList.contains(color))
                    {
                        colorList.add(color);
                        colorAdapter.notifyDataSetChanged();
                    } else{
                        Log.w(TAG, "Color already entered");
                    }
                }
                break;
            case COLOR_REMOVER_FRAGMENT:
                if(resultCode == Activity.RESULT_OK) {
                    // remove from arraylist
                    colorList.remove(bundle.getInt("colorIndex"));
                    if(colorList.isEmpty()){
                        // make sure we at least have one color
                        colorList.add(Color.WHITE);
                    }
                    colorAdapter.notifyDataSetChanged();
                }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        // save all form data
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("setDate", setDate);
        editor.putInt("area", ((Spinner) getActivity().findViewById(R.id.areaSpinner)).getSelectedItemPosition());
        editor.putInt("grade", ((Spinner)getActivity().findViewById(R.id.gradeSpinner)).getSelectedItemPosition());
        editor.commit();

        // update color database
        mDbSource.replaceColors(colorList);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnAddClimbFragmentInteractionListener {
        public GymLocalDbSource getDbSource();
    }


}
