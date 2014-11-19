package com.grant.gymclimbtracker.gym_client;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.grant.gymclimbtracker.climb_contract.ClimbContract;


public class GymListFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final int CLIMB_LIST_ID = 1;
    private static final String TAG = "GymListFragment";
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //--private static final String ARG_PARAM1 = "param1";

    //--private String mParam1;

    private OnGymListFragmentInteractionListener mListener;
    private ContentResolver resolver;

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
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
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
        return inflater.inflate(R.layout.fragment_gym_list, container, false);
    }

    @Override
   public void onAttach(Activity activity) {
        super.onAttach(activity);
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
        mListener = null;
    }

    @Override
    public String toString() {
        return "List of Climbs";
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        resolver = getActivity().getContentResolver();
        getLoaderManager().initLoader(CLIMB_LIST_ID, null, this);
        registerForContextMenu(getListView());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if(getActivity()!= null){
            return new CursorLoader(this.getActivity(),
                    ClimbContract.Climbs.CONTENT_URI,
                    ClimbContract.Climbs.PROJECTION_ALL,
                    null,
                    null,
                    ClimbContract.Climbs.SORT_ORDER_DEFAULT);
        }
        else{
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(getListAdapter()==null) {
            ClimbCursorAdapter adapter = new ClimbCursorAdapter(getActivity(), cursor, 0);
            this.setListAdapter(adapter);
        }
         else{
            ((ClimbCursorAdapter)this.getListAdapter()).swapCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((SimpleCursorAdapter)this.getListAdapter()).swapCursor(null);
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
