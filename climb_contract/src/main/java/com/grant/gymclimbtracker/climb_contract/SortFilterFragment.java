package com.grant.gymclimbtracker.climb_contract;

import android.app.Activity;
 import android.content.SharedPreferences;
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
 import android.widget.TextView;
 import android.widget.ToggleButton;

 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
import java.util.Map;


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
     private static final String PREFS_NAME = "SortFilterPrefs";
     // TODO: Rename parameter arguments, choose names that match
     // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

     private DialogFragmentHandler mListener;
     private Spinner gradeFilterSpinner;
     private Spinner areaFilterSpinner;
     private Spinner typeFilterSpinner;
     final modeType[] modeVals = modeType.values();
     final ClimbContract.climbType[] typeVals = ClimbContract.climbType.values();
     private ArrayList<String> sortOrderList = new ArrayList<String>();
     private static final Map<String, Integer> contractToView;// convert view to contract, for sort order
     static{
         Map<String, Integer> temp = new HashMap<String, Integer>();
         temp.put(ClimbContract.Climbs.AREA,R.id.areaOrderToggleButton);
         temp.put(ClimbContract.Climbs.GRADE,R.id.gradeOrderToggleButton);
         temp.put( ClimbContract.Climbs.DATE_SET,R.id.dateOrderToggleButton);

         contractToView = Collections.unmodifiableMap(temp);
     }

     private SharedPreferences settings;
     private Spinner areaModeSpinner;
     private Spinner gradeModeSpinner;

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
             // get sharedpref editor to save rope/grade settings
             SharedPreferences.Editor editor = settings.edit();

             // change fields according to climb type
             if(position == ClimbContract.climbType.toprope.ordinal() || position == ClimbContract.climbType.lead.ordinal()) {
                 // save current area/grade
                 editor.putInt("boulderArea", areaFilterSpinner.getSelectedItemPosition());
                 editor.putInt("boulderGrade", gradeFilterSpinner.getSelectedItemPosition());

                 // set items in spinner
                 gradeFilterSpinner.setAdapter(new ArrayAdapter<ClimbContract.ropeGrade>(getActivity(), android.R.layout.simple_spinner_item, ClimbContract.ropeGrade.values()));
                 areaFilterSpinner.setAdapter(ArrayAdapter.createFromResource(getActivity(), R.array.ropeAreas, android.R.layout.simple_spinner_item));

                 // restore previous roped grade/area
                 gradeFilterSpinner.setSelection(settings.getInt("ropeGrade", 0));
                 areaFilterSpinner.setSelection(settings.getInt("ropeArea",0));
             }else if (position == ClimbContract.climbType.boulder.ordinal()) {
                 // save current area/grade
                 editor.putInt("ropeArea", areaFilterSpinner.getSelectedItemPosition());
                 editor.putInt("ropeGrade", gradeFilterSpinner.getSelectedItemPosition());

                 // set items in spinner
                 gradeFilterSpinner.setAdapter(new ArrayAdapter<ClimbContract.boulderGrade>(getActivity(), android.R.layout.simple_spinner_item, ClimbContract.boulderGrade.values()));
                 areaFilterSpinner.setAdapter(ArrayAdapter.createFromResource(getActivity(), R.array.boulderAreas, android.R.layout.simple_spinner_item));

                 // restore previous boulder grade/area
                 gradeFilterSpinner.setSelection(settings.getInt("boulderGrade", 0));
                 areaFilterSpinner.setSelection(settings.getInt("boulderArea",0));
             }
             editor.putInt("typeFilter", position);
             editor.commit();
         }

         /**
          * If the mode spinners changed, modify the appropriate views
          */
         else if(viewId == R.id.areaModeSpinner) {
             // remove from sort order
             if(sortOrderList.contains(ClimbContract.Climbs.AREA)) {
               sortOrderList.remove(ClimbContract.Climbs.AREA);
           }
             switch(modeVals[position]) {
                 case none: // Don't use filter/sort
                     getView().findViewById(R.id.areaOrderToggleButton).setVisibility(View.GONE);
                     getView().findViewById(R.id.areaFilterSpinner).setVisibility(View.GONE);
                     break;
                 case sort: // Sort
                     // add it to sort order
                     sortOrderList.add(ClimbContract.Climbs.AREA);

                     getView().findViewById(R.id.areaOrderToggleButton).setVisibility(View.VISIBLE);
                     getView().findViewById(R.id.areaFilterSpinner).setVisibility(View.GONE);
                     break;
                 case filter: // Filter
                     getView().findViewById(R.id.areaOrderToggleButton).setVisibility(View.GONE);
                     getView().findViewById(R.id.areaFilterSpinner).setVisibility(View.VISIBLE);
                     break;
             }
         }else if(viewId == R.id.gradeModeSpinner) {
             // remove from sort order
             if (sortOrderList.contains(ClimbContract.Climbs.GRADE)) {
                               sortOrderList.remove(ClimbContract.Climbs.GRADE);
                           }
             switch (modeVals[position]) {
                 case none: // Don't use filter/sort
                     getView().findViewById(R.id.gradeOrderToggleButton).setVisibility(View.GONE);
                     getView().findViewById(R.id.gradeFilterSpinner).setVisibility(View.GONE);
                     break;
                 case sort: // Sort
                     // add it to sort order
                     sortOrderList.add(ClimbContract.Climbs.GRADE);

                     getView().findViewById(R.id.gradeOrderToggleButton).setVisibility(View.VISIBLE);
                     getView().findViewById(R.id.gradeFilterSpinner).setVisibility(View.GONE);
                     break;
                 case filter: // Filter
                     getView().findViewById(R.id.gradeOrderToggleButton).setVisibility(View.GONE);
                     getView().findViewById(R.id.gradeFilterSpinner).setVisibility(View.VISIBLE);
                     break;
             }
         }
         // date always comes last
         if(sortOrderList.contains(ClimbContract.Climbs.DATE_SET)){
             sortOrderList.remove(ClimbContract.Climbs.DATE_SET);
         }
         sortOrderList.add(ClimbContract.Climbs.DATE_SET);

         // set the text string
         ((TextView) (getView().findViewById(R.id.sortOrderTextView))).setText(getSortOrderString());
     }

    private String getSortOrderString() {
        String sortOrderText= "";
        for(String string:sortOrderList){
            if(!sortOrderText.isEmpty()){
                sortOrderText += " -> ";
            }
            sortOrderText += string;
        }
        return sortOrderText;
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

         // get sharedpref
         settings = getActivity().getSharedPreferences(PREFS_NAME, 0);


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

         areaModeSpinner = (Spinner) v.findViewById(R.id.areaModeSpinner);
         areaModeSpinner.setAdapter(adapter);
         areaModeSpinner.setOnItemSelectedListener(this);

         gradeModeSpinner = (Spinner) v.findViewById(R.id.gradeModeSpinner);
         gradeModeSpinner.setAdapter(adapter);
         gradeModeSpinner.setOnItemSelectedListener(this);

         /**
          * populate climb type Spinner
          * When this populates, it will call the onItemSelected method that will populate the other spinners
          */
         typeFilterSpinner = (Spinner) v.findViewById(R.id.typeFilterSpinner);
         typeFilterSpinner.setAdapter(new ArrayAdapter<ClimbContract.climbType>(getActivity(), android.R.layout.simple_spinner_item, ClimbContract.climbType.values()));
         typeFilterSpinner.setOnItemSelectedListener(this);

         gradeFilterSpinner = (Spinner) v.findViewById(R.id.gradeFilterSpinner);
         areaFilterSpinner = (Spinner) v.findViewById(R.id.areaFilterSpinner);

         /**
          * set current pref
          * grade and area presets will be set when onItemSelected is called
          */
         gradeModeSpinner.setSelection(settings.getInt("gradeMode",0),true);
         areaModeSpinner.setSelection(settings.getInt("areaMode",0),true);
         typeFilterSpinner.setSelection(settings.getInt("typeFilter",0),true);
         ((ToggleButton)v.findViewById(R.id.areaOrderToggleButton)).setChecked(settings.getBoolean("areaOrder",true));
         ((ToggleButton)v.findViewById(R.id.gradeOrderToggleButton)).setChecked(settings.getBoolean("gradeOrder",true));
         ((ToggleButton)v.findViewById(R.id.dateOrderToggleButton)).setChecked(settings.getBoolean("dateOrder",true));

         // get sortorderlist from shared pref
         loadSortOrderList();


         ((TextView)(v.findViewById(R.id.sortOrderTextView))).setText(getSortOrderString());
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
                       public void onPause() {
                           super.onPause();
                           Log.i(TAG, "onPause");

                           // save form data
                           SharedPreferences.Editor editor = settings.edit();
                           editor.putInt("gradeMode", gradeModeSpinner.getSelectedItemPosition());
                           editor.putInt("areaMode", areaModeSpinner.getSelectedItemPosition());
                           editor.putBoolean("areaOrder", ((ToggleButton) getView().findViewById(R.id.areaOrderToggleButton)).isChecked());
                           editor.putBoolean("gradeOrder", ((ToggleButton) getView().findViewById(R.id.gradeOrderToggleButton)).isChecked());
                           editor.putBoolean("dateOrder", ((ToggleButton) getView().findViewById(R.id.dateOrderToggleButton)).isChecked());

         saveSortOrderList();
                           editor.commit();
                       }

     @Override
                            public void onClick(View v) {
                                Log.i(TAG, "onClick");
                                if(mListener!=null){

                                    Bundle b = new Bundle();
                                    int viewId = v.getId();

                                    if(viewId == R.id.sortFiltOkayButton) {
                                        // build up SQL string and pass to handler


                                        String mSelection;

                                        switch (typeVals[typeFilterSpinner.getSelectedItemPosition()]) {
                                            case boulder:
                                                mSelection = ClimbContract.Climbs.TYPE + " = " + ClimbContract.climbType.boulder.ordinal();
                                                break;
                                            case lead:
                                                mSelection = ClimbContract.Climbs.TYPE + " = " + ClimbContract.climbType.lead.ordinal();
                                                break;
                                            case toprope:
                                                mSelection = ClimbContract.Climbs.TYPE + " = " + ClimbContract.climbType.toprope.ordinal();
                                                break;
                                            default:
                                                throw new IllegalArgumentException("Unrecognized type filter selection");
                                        }

                                        /**
                                         * Add filters
                                         */
                                        if(modeVals[((Spinner)getView().findViewById(R.id.areaModeSpinner)).
                                                getSelectedItemPosition()] == modeType.filter){
                                                mSelection += " AND " + ClimbContract.Climbs.AREA + " = '" + (areaFilterSpinner.getSelectedItem()).toString()+"'";
                                        }
                                        if(modeVals[((Spinner)getView().findViewById(R.id.gradeModeSpinner)).
                                                getSelectedItemPosition()] == modeType.filter){
                                                mSelection += " AND " + ClimbContract.Climbs.GRADE + " = " + gradeFilterSpinner.getSelectedItemPosition();
                                        }

                                        /**
                                         * Create sort order string using the sortorder list
                                         */
                                        String mSortOrder = "";
                                        for(String sortItem:sortOrderList) {
                                            if(!mSortOrder.isEmpty()){
                                                mSortOrder += ", ";
                                            }

                                            // add contract item associated with sort
                                            mSortOrder += sortItem;
                                            if(((ToggleButton)getView().findViewById(contractToView.get(sortItem))).isChecked()) {
                                                mSortOrder += " ASC";
                                            }else{
                                                mSortOrder += " DESC";
                                            }
                                        }
                                        // always use date as last sort item
                                        if(!mSortOrder.isEmpty()){
                                            mSortOrder += ", ";
                                        }
                                        mSortOrder += ClimbContract.Climbs.DATE_SET;
                                        if(((ToggleButton)getView().findViewById(R.id.dateOrderToggleButton)).isChecked()) {
                                            mSortOrder += " ASC";
                                        }else{
                                            mSortOrder += " DESC";
                                        }

                                        b.putString("mSelection", mSelection);
                                        b.putString("mSortOrder", mSortOrder);
                                        mListener.handleDialogResult(getTargetRequestCode(), Activity.RESULT_OK, b);

                                    }else if(viewId == R.id.sortFiltCancelButton){
                                        mListener.handleDialogResult(getTargetRequestCode(), Activity.RESULT_CANCELED, b);
                                    }
                                }
                                dismiss();

                            }

    public boolean saveSortOrderList()
              {
                  SharedPreferences.Editor editor = settings.edit();
                  editor.putInt("sortOrderSize", sortOrderList.size()); /* sKey is an array */

                  for(int i=0;i<sortOrderList.size();i++)
                  {
                      editor.remove("sortOrder_" + i);
                      editor.putString("sortOrder_" + i, sortOrderList.get(i));
                  }

                  return editor.commit();
              }

    public void loadSortOrderList()
         {
             sortOrderList.clear();
             int size = settings.getInt("sortOrderSize", 0);

             for(int i=0;i<size;i++)
             {
                 sortOrderList.add(settings.getString("sortOrder_" + i, null));
             }
         }

 }
