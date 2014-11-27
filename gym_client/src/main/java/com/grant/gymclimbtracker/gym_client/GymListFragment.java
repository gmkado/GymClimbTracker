package com.grant.gymclimbtracker.gym_client;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;

import com.grant.gymclimbtracker.climb_contract.ClimbContract;
import com.grant.gymclimbtracker.climb_contract.DialogFragmentHandler;
import com.grant.gymclimbtracker.climb_contract.SortFilterFragment;


public class GymListFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener, DialogFragmentHandler {
    private static final int CLIMB_LIST_ID = 1;
    private static final String TAG = "GymListFragment";
    private static final int SORT_FILTER_FRAGMENT = 1;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //--private static final String ARG_PARAM1 = "param1";

    //--private String mParam1;

    private OnGymListFragmentInteractionListener mListener;
    private ContentResolver resolver;
    private String mSelection;
    private String mSortOrder;
    private String mProviderSelection;
    private String mLocalSelection;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddClimbFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GymListFragment newInstance() {
        GymListFragment fragment = new GymListFragment();
        //--Bundle args = new Bundle();
        //--args.putString(ARG_PARAM1, param1);

        //--fragment.setArguments(args);
        return fragment;
    }
    public GymListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        Log.i(TAG, "onCreateContextMenu");
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
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
        View v = inflater.inflate(R.layout.fragment_gym_list, container, false);
        ImageButton button = (ImageButton)v.findViewById(R.id.sortFilterButton);
        button.setOnClickListener(this);

        return v;
    }

    @Override
   public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(TAG, "onAttach");
        try {
            mListener = (OnGymListFragmentInteractionListener) activity;
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
        // content resolver for web db
        resolver = getActivity().getContentResolver();
        getLoaderManager().initLoader(CLIMB_LIST_ID, null, this);

        // now that the activity is created, register list items for context menu
        registerForContextMenu(getListView());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if(getActivity()!= null){
            return new CursorLoader(this.getActivity(),
                    ClimbContract.Climbs.CONTENT_URI,
                    ClimbContract.Climbs.PROJECTION_ALL,
                    mSelection,
                    null,
                    mSortOrder);
        }
        else{
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(getListAdapter()==null) {
            GymCursorAdapter adapter = new GymCursorAdapter(getActivity(), cursor, 0);
            this.setListAdapter(adapter);
        }
         else{
            ((GymCursorAdapter)this.getListAdapter()).swapCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((GymCursorAdapter)this.getListAdapter()).swapCursor(null);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.sortFilterButton) {
            // open sort/filter dialog
            // open sort filter dialog fragment
            SortFilterFragment fragment = SortFilterFragment.newInstance();
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
         public interface OnGymListFragmentInteractionListener {
             public void onGymListFragmentInteraction(Uri uri);
         }

    @Override
         public boolean onContextItemSelected(MenuItem item) {
             AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

             switch (item.getItemId()) {
                 case R.id.delete:
                     // delete the item
                     Uri delUri = ContentUris.withAppendedId(ClimbContract.Climbs.CONTENT_URI, info.id);
                     int delCount = resolver.delete(delUri, null, null);
                     if(delCount == 0){
                         Log.d(TAG, "No item deleted");
                     }else if (delCount == 1) {
                         Log.d(TAG, "Item deleted successfully");
                     }else{
                         Log.d(TAG, "Something went wrong. delCount = " + delCount);
                     }
                     return true;
                 default:
                     return super.onContextItemSelected(item);
             }
         }

}
