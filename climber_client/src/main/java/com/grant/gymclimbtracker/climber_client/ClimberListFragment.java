package com.grant.gymclimbtracker.climber_client;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.grant.gymclimbtracker.climb_contract.ClimbContract;
import com.grant.gymclimbtracker.climb_contract.DialogFragmentHandler;
import com.grant.gymclimbtracker.climb_contract.ShowMapFragment;
import com.grant.gymclimbtracker.climb_contract.SortFilterFragment;


public class ClimberListFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemSelectedListener, View.OnClickListener, DialogFragmentHandler{
    private static final int CLIMB_LIST_ID = 1;
    private static final String TAG = "ClimberListFragment";
    private static final int SORT_FILTER_FRAGMENT = 1;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //--private static final String ARG_PARAM1 = "param1";

    //--private String mParam1;

    private OnClimberListFragmentInteractionListener mListener;
    private ContentResolver resolver;
    private ClimberLocalDbSource mDbSource;
    private String[] mSelectionArgs;
    private String mSelection;
    private String mSortOrder = ClimbContract.Climbs.SORT_ORDER_DEFAULT;
    private String mLocalSelection;
    private String mProviderSelection;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        DialogFragment fragment;
        if(id == R.id.showMapButton){
            fragment = ShowMapFragment.newInstance();
            fragment.show(getActivity().getSupportFragmentManager(), "showMap");
        }else if(id == R.id.sortFiltButton) {
            // open sort filter dialog fragment
            fragment = SortFilterFragment.newInstance();
            fragment.setTargetFragment(this, SORT_FILTER_FRAGMENT);
            fragment.show(getActivity().getSupportFragmentManager(), "sortFilter");
        }
    }

    @Override
    public void handleDialogResult(int RequestCode, int ResultCode, Bundle data) {
        switch (RequestCode){
            case SORT_FILTER_FRAGMENT:
                if(ResultCode == Activity.RESULT_OK) {
                    // use the result string to sort the list
                    mProviderSelection = data.getString("mSelection");
                    mSortOrder = data.getString("mSortOrder");
                    refreshList();
                }
                break;
        }
    }

    /**
     * Enum definitions for sort
     */
    public enum sortBy{
        // type of climb
        grade("Grade"),
        area("Area"),
        dateSet("Date");

        private String label;

        private sortBy(String label){
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    /**
     * Enum definitions for filter
     */
    public enum filterBy{
        // type of climb
        showAll("Show All"),
        projects("Projects"),
        unsent("Unsent"),
        sent("Sent");

        private String label;

        private filterBy(String label){
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public ClimberListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddClimbFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClimberListFragment newInstance() {
        ClimberListFragment fragment = new ClimberListFragment();
        //--Bundle args = new Bundle();
        //--args.putString(ARG_PARAM1, param1);

        //--fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        // get the listitem view from the menuinfo
        View target = ((AdapterView.AdapterContextMenuInfo)menuInfo).targetView;

        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);

        // set text for Add/Remove project
        MenuItem menuItem = menu.findItem(R.id.addRemoveProjects);
        int id = Integer.parseInt((String) ((TextView) target.findViewById(R.id.idListItem)).getText());

        if(mDbSource.isProject(id)) {
            menuItem.setTitle(getString(R.string.removeProject));
        }else{
            menuItem.setTitle(getString(R.string.addProject));
        }

        menuItem = menu.findItem(R.id.addRemoveSent);
        if(mDbSource.isSent(id)) {
            menuItem.setTitle(getString(R.string.unsendIt));
        }else{
            menuItem.setTitle(getString(R.string.sentIt));
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        /*--if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);

        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_climber_list, container, false);

        Spinner spinner = ((Spinner)v.findViewById(R.id.filterLocalSpinner));
        spinner.setAdapter(new ArrayAdapter<filterBy>(getActivity(),android.R.layout.simple_spinner_dropdown_item,filterBy.values()));
        spinner.setOnItemSelectedListener(this);

        v.findViewById(R.id.sortFiltButton).setOnClickListener(this);

        v.findViewById(R.id.showMapButton).setOnClickListener(this);
        return v;
    }

    @Override
   public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(TAG, "onAttach");
        try {
            mListener = (OnClimberListFragmentInteractionListener) activity;
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
        return "List of Climbs";
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated");
        // content resolver for web database
        resolver = getActivity().getContentResolver();
        getLoaderManager().initLoader(CLIMB_LIST_ID, null, this);

        // get local database source from main activity
        mDbSource = mListener.getDbSource();

        // now that activity is created, register the view for context menu
        registerForContextMenu(getListView());

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if(getActivity()!= null){
            return new CursorLoader(this.getActivity(),
                    ClimbContract.Climbs.CONTENT_URI,
                    ClimbContract.Climbs.PROJECTION_ALL,
                    mSelection,
                    mSelectionArgs,
                    mSortOrder
                    );
        }
        else{
            return null;
        }
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(getListAdapter()==null) {
            ClimberCursorAdapter adapter = new ClimberCursorAdapter(getActivity(), cursor, 0);
            this.setListAdapter(adapter);
        }
         else{
            ((ClimberCursorAdapter)this.getListAdapter()).swapCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((ClimberCursorAdapter)this.getListAdapter()).swapCursor(null);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        final filterBy[] filtVals = filterBy.values();
        switch (parent.getId()) {
            case R.id.filterLocalSpinner:
                switch (filtVals[((Spinner) getActivity().findViewById(R.id.filterLocalSpinner)).getSelectedItemPosition()]) {
                    case showAll: // show all
                        mLocalSelection = "";
                        break;
                    case projects:
                        // show projects
                        // get the project ids
                        mLocalSelection = ClimbContract.Climbs._ID + " IN " + mDbSource.getProjectIds();
                        break;
                    case unsent:
                        // show only unsent
                        mLocalSelection = ClimbContract.Climbs._ID + " NOT IN " + mDbSource.getSentIds();
                        break;
                    case sent:
                        // show only sent
                        mLocalSelection = ClimbContract.Climbs._ID + " IN " + mDbSource.getSentIds();
                        break;
                    default:
                        throw new IllegalArgumentException("Unrecognized filter selection");
                }
                refreshList();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // get the id of the list item selected
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        TextView tv = (TextView)(info.targetView.findViewById(R.id.idListItem));
        int id = Integer.parseInt((String)tv.getText());

        switch (item.getItemId()) {
            case R.id.addRemoveProjects:
                // add to projects
                mDbSource.addRemoveProject(id);
                break;
            case R.id.workinIt:
                mDbSource.incrementAttemptCount(id);
                break;
            case R.id.addRemoveSent:
                // get grade and type of selected climb
                Cursor cursor = resolver.query(ClimbContract.Climbs.CONTENT_URI,
                        new String[] {ClimbContract.Climbs.TYPE, ClimbContract.Climbs.GRADE},
                        ClimbContract.Climbs._ID + " = " + id,
                        null, null);
                cursor.moveToFirst();
                mDbSource.addRemoveSent(id,
                        cursor.getInt(cursor.getColumnIndex(ClimbContract.Climbs.TYPE)),
                        cursor.getInt(cursor.getColumnIndex(ClimbContract.Climbs.GRADE)));
                break;
            default:
                return super.onContextItemSelected(item);
        }
        refreshList();
        return true;
    }

    private void refreshList() {
        mSelection = "";

        if(mProviderSelection != null && !mProviderSelection.isEmpty()) {
            mSelection = mProviderSelection;
        }
        if(mLocalSelection != null && !mLocalSelection.isEmpty()){
            if(!mSelection.isEmpty()) {
                mSelection += " AND ";
            }
            mSelection += mLocalSelection;
        }


        getLoaderManager().restartLoader(CLIMB_LIST_ID,null,this);
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
    public interface OnClimberListFragmentInteractionListener {
        public void onGymListFragmentInteraction(Uri uri);

        public ClimberLocalDbSource getDbSource();
    }

}
