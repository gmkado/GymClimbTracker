package com.grant.gymclimbtracker.gym_client;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;

import java.text.SimpleDateFormat;
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
        implements Spinner.OnItemSelectedListener, Button.OnClickListener, ColorPicker.OnColorSelectedListener, DatePickerDialog.OnDateSetListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //--private static final String ARG_PARAM1 = "param1";

    //--private String mParam1;

    private OnAddClimbFragmentInteractionListener mListener;
    private Spinner gradeSpinner;
    private Spinner locationSpinner;
    private ColorPicker picker;
    private int currentColor;
    private String setDate;
    private Calendar mCalendar;
    private SimpleDateFormat mDateFormat;
    private ContentResolver resolver;

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
        /*--if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);

        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_climb, container, false);

        /**
         * Set datebutton text to current date and attach listener
         */
        mCalendar = Calendar.getInstance();
        Button button = (Button)v.findViewById(R.id.dateSelectButton);
        mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        setDate = mDateFormat.format(mCalendar.getTime());
        button.setText(setDate);
        button.setOnClickListener(this);

        // attach listener to addclimb button
        button = (Button)v.findViewById(R.id.addClimbButton);
        button.setOnClickListener(this);

        button = (Button)v.findViewById(R.id.showMapButton);
        button.setOnClickListener(this);


        /**
         * populate climb type Spinner
         * When this populates, it will call the onItemSelected method that will populate the other spinners
          */
        Spinner typeSpinner = (Spinner) v.findViewById(R.id.typeSpinner);
        typeSpinner.setAdapter(new ArrayAdapter<ClimbContract.climbType>(getActivity(), android.R.layout.simple_spinner_item, ClimbContract.climbType.values()));
        typeSpinner.setOnItemSelectedListener(this);

        // set behavior of color wheel
        picker = (ColorPicker)v.findViewById(R.id.picker);
        picker.addSVBar((SVBar) v.findViewById(R.id.svbar));
        picker.setOnColorSelectedListener(this);

        resolver = getActivity().getContentResolver();
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

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
                if(locationSpinner == null){
                    locationSpinner = (Spinner) getActivity().findViewById(R.id.areaSpinner);
                }

                // change fields according to climb type
                if(position == ClimbContract.climbType.toprope.ordinal() || position == ClimbContract.climbType.lead.ordinal()) {
                    gradeSpinner.setAdapter(new ArrayAdapter<ClimbContract.ropeGrade>(getActivity(), android.R.layout.simple_spinner_item, ClimbContract.ropeGrade.values()));
                    locationSpinner.setAdapter(ArrayAdapter.createFromResource(getActivity(), R.array.PGSV_ropeLoc, android.R.layout.simple_spinner_item));
                }else if (position == ClimbContract.climbType.boulder.ordinal()) {
                    gradeSpinner.setAdapter(new ArrayAdapter<ClimbContract.boulderGrade>(getActivity(), android.R.layout.simple_spinner_item, ClimbContract.boulderGrade.values()));
                    locationSpinner.setAdapter(ArrayAdapter.createFromResource(getActivity(), R.array.PGSV_boulderLoc, android.R.layout.simple_spinner_item));

                }
                break;


        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onColorSelected(int color) {
        if(currentColor == 0) {
            picker.setOldCenterColor(color);
        }else {
            picker.setOldCenterColor(currentColor);
        }
        currentColor = color;

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
            case R.id.addClimbButton:
                ContentValues c = new ContentValues();
                Activity activity = getActivity();
                c.put(ClimbContract.Climbs.GYM, getString(R.string.gym_name));
                c.put(ClimbContract.Climbs.AREA, ((Spinner)activity.findViewById(R.id.areaSpinner)).getSelectedItem().toString());
                c.put(ClimbContract.Climbs.TYPE, ((Spinner)activity.findViewById(R.id.typeSpinner)).getSelectedItemPosition());
                c.put(ClimbContract.Climbs.GRADE, ((Spinner)activity.findViewById(R.id.gradeSpinner)).getSelectedItemPosition());
                c.put(ClimbContract.Climbs.DATE_SET, setDate);
                c.put(ClimbContract.Climbs.COLOR_PRIMARY, picker.getColor());
                c.put(ClimbContract.Climbs.COLOR_SECONDARY, picker.getOldCenterColor());

                resolver.insert(ClimbContract.Climbs.CONTENT_URI, c);
                break;

            default:
                throw new IllegalArgumentException("Unexpected onClick view");

        }
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
        public void onAddClimbFragmentInteraction(Uri uri);
    }


}
