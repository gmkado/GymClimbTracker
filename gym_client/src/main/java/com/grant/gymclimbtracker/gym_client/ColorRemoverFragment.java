package com.grant.gymclimbtracker.gym_client;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import com.grant.gymclimbtracker.climb_contract.DialogFragmentHandler;
import com.grant.gymclimbtracker.gym_client.ColorPickerFragment.*;

/**
 * A simple {@link android.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DialogFragmentHandler} interface
 * to handle interaction events.
 * Use the {@link com.grant.gymclimbtracker.gym_client.ColorRemoverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ColorRemoverFragment extends DialogFragment implements AdapterView.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER




    private DialogFragmentHandler mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ColorPickerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ColorRemoverFragment newInstance(ArrayList<Integer> colors) {
        ColorRemoverFragment fragment = new ColorRemoverFragment();
        Bundle args = new Bundle();
        args.putIntegerArrayList("colorList", colors);
        fragment.setArguments(args);
        return fragment;
    }

    public ColorRemoverFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_color_remover, container, false);
        ListView lv = (ListView)v.findViewById(R.id.colorListView);

        ColorSpinnerAdapter adapter = new ColorSpinnerAdapter(getActivity(),getArguments().getIntegerArrayList("colorList"));
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        return v;
    }
    
    @Override
    public void onAttach(Activity activity) {
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
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle b = new Bundle();
        b.putInt("colorIndex", position);
        mListener.handleDialogResult(getTargetRequestCode(), Activity.RESULT_OK,  b);
        dismiss();
    }
}
