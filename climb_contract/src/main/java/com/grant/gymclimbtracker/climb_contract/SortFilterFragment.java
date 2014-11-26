package com.grant.gymclimbtracker.climb_contract;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


/**
 * A simple {@link android.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DialogFragmentHandler} interface
 * to handle interaction events.
 * Use the {@link com.grant.gymclimbtracker.climb_contract.SortFilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SortFilterFragment extends DialogFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = "SortFilterFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private DialogFragmentHandler mListener;
    private Spinner gradeFilterSpinner;
    private Spinner areaFilterSpinner;
    private Spinner typeFilterSpinner;
    final modeType[] modeVals = modeType.values();
    final ClimbContract.climbType[] typeVals = ClimbContract.climbType.values();

    private enum modeType {
        none("Not used"),
        sort("Sort"),
        filter("Filter");

        private String name;

        private modeType (String name) {
            this.name = name;
        }

        @Override
        public String toString(){
            return name;
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        Log.i(TAG, "OnItemSelected");

        int viewId = adapterView.getId();

        // if the type of climb filter was changed
        if (viewId == R.id.typeFilterSpinner) {
            // change fields according to climb type
            if (position == ClimbContract.climbType.toprope.ordinal() || position == ClimbContract.climbType.lead.ordinal()) {
                gradeFilterSpinner.setAdapter(new ArrayAdapter<ClimbContract.ropeGrade>(getActivity(), android.R.layout.simple_spinner_item, ClimbContract.ropeGrade.values()));
                areaFilterSpinner.setAdapter(ArrayAdapter.createFromResource(getActivity(), R.array.ropeAreas, android.R.layout.simple_spinner_item));
            } else if (position == ClimbContract.climbType.boulder.ordinal()) {
                gradeFilterSpinner.setAdapter(new ArrayAdapter<ClimbContract.boulderGrade>(getActivity(), android.R.layout.simple_spinner_item, ClimbContract.boulderGrade.values()));
                areaFilterSpinner.setAdapter(ArrayAdapter.createFromResource(getActivity(), R.array.boulderAreas, android.R.layout.simple_spinner_item));
            }
        }

        /**
         * If the mode spinners changed, modify the appropriate views
         */
        else if(viewId == R.id.areaModeSpinner) {
            switch(modeVals[position]) {
                case none: // Don't use filter/sort
                    getView().findViewById(R.id.areaOrderToggleButton).setVisibility(View.GONE);
                    getView().findViewById(R.id.areaFilterSpinner).setVisibility(View.GONE);
                    break;
                case sort: // Sort
                    getView().findViewById(R.id.areaOrderToggleButton).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.areaFilterSpinner).setVisibility(View.GONE);
                    break;
                case filter: // Filter
                    getView().findViewById(R.id.areaOrderToggleButton).setVisibility(View.GONE);
                    getView().findViewById(R.id.areaFilterSpinner).setVisibility(View.VISIBLE);
                    break;
            }
        }else if(viewId == R.id.gradeModeSpinner) {
            switch(modeVals[position]) {
                case none: // Don't use filter/sort
                    getView().findViewById(R.id.gradeOrderToggleButton).setVisibility(View.GONE);
                    getView().findViewById(R.id.gradeFilterSpinner).setVisibility(View.GONE);
                    break;
                case sort: // Sort
                    getView().findViewById(R.id.gradeOrderToggleButton).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.gradeFilterSpinner).setVisibility(View.GONE);
                    break;
                case filter: // Filter
                    getView().findViewById(R.id.gradeOrderToggleButton).setVisibility(View.GONE);
                    getView().findViewById(R.id.gradeFilterSpinner).setVisibility(View.VISIBLE);
                    break;
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.i(TAG, "onNothingSelected");

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ColorPickerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SortFilterFragment newInstance() {
        SortFilterFragment fragment = new SortFilterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SortFilterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_sort_filter, container, false);

        Button b = (Button)v.findViewById(R.id.sortFiltOkayButton);
        b.setOnClickListener(this);

        b = (Button)v.findViewById(R.id.sortFiltCancelButton);
        b.setOnClickListener(this);

        // populate all the "mode" spinners
        ArrayAdapter<modeType> adapter = new ArrayAdapter<modeType>(getActivity(), android.R.layout.simple_list_item_1, modeType.values());

        Spinner spinner = (Spinner) v.findViewById(R.id.areaModeSpinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        spinner = (Spinner) v.findViewById(R.id.gradeModeSpinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        /**
         * populate climb type Spinner
         * When this populates, it will call the onItemSelected method that will populate the other spinners
         */
        typeFilterSpinner = (Spinner) v.findViewById(R.id.typeFilterSpinner);
        typeFilterSpinner.setAdapter(new ArrayAdapter<ClimbContract.climbType>(getActivity(), android.R.layout.simple_spinner_item, ClimbContract.climbType.values()));
        typeFilterSpinner.setOnItemSelectedListener(this);

        gradeFilterSpinner = (Spinner) v.findViewById(R.id.gradeFilterSpinner);
        areaFilterSpinner = (Spinner) v.findViewById(R.id.areaFilterSpinner);
        return v;
    }
    
    @Override
    public void onAttach(Activity activity) {
        Log.i(TAG, "onAttach");
        super.onAttach(activity);
        try {
            mListener = (DialogFragmentHandler) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement DialogFragmentHandler");
        }
    }

    @Override
    public void onDetach() {
        Log.i(TAG, "onDetach");
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "onClick");
        if(mListener!=null){

            Bundle b = new Bundle();
            int viewId = v.getId();

            if(viewId == R.id.sortFiltOkayButton) {
                // TODO:build up SQL string and pass to handler


                String mSelection = "";
                switch (typeVals[typeFilterSpinner.getSelectedItemPosition()]) {
                    case boulder:
                        mSelection = mSelection + ClimbContract.Climbs.TYPE + " = " + ClimbContract.climbType.boulder.ordinal();
                        break;
                    case lead:
                        mSelection = mSelection + ClimbContract.Climbs.TYPE + " = " + ClimbContract.climbType.lead.ordinal();
                        break;
                    case toprope:
                        mSelection = mSelection + ClimbContract.Climbs.TYPE + " = " + ClimbContract.climbType.toprope.ordinal();
                        break;
                    default:
                        throw new IllegalArgumentException("Unrecognized type filter selection");
                }

                /**
                 * Parse area views
                 */
                switch(modeVals[((Spinner)getView().findViewById(R.id.areaModeSpinner)).getSelectedItemPosition()]){
                    case filter:
                        mSelection = mSelection + " AND " + ClimbContract.Climbs.AREA + " = " + (areaFilterSpinner.getSelectedItem()).toString();
                        break;
                    case sort:
                        // add to sort string
                        break;
                }

                /**
                 * Parse grade views
                 */
                switch(modeVals[((Spinner)getView().findViewById(R.id.gradeModeSpinner)).getSelectedItemPosition()]){
                    case filter:
                        mSelection = mSelection + " AND " + ClimbContract.Climbs.GRADE + " = " + gradeFilterSpinner.getSelectedItemPosition();
                        break;
                    case sort:
                        // add to sort string
                        break;
                }



                b.putString("mSelection", mSelection);
                mListener.handleDialogResult(getTargetRequestCode(), Activity.RESULT_OK, b);

            }else if(viewId == R.id.sortFiltCancelButton){
                mListener.handleDialogResult(getTargetRequestCode(), Activity.RESULT_CANCELED, b);
            }
        }
        dismiss();

    }

}
