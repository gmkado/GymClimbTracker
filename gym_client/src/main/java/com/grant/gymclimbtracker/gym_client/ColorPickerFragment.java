package com.grant.gymclimbtracker.gym_client;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.grant.gymclimbtracker.climb_contract.DialogFragmentHandler;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.grant.gymclimbtracker.climb_contract.DialogFragmentHandler} interface
 * to handle interaction events.
 * Use the {@link ColorPickerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ColorPickerFragment extends DialogFragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER




    private DialogFragmentHandler mListener;
    private ColorPicker picker;
    private int currentColor;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ColorPickerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ColorPickerFragment newInstance() {
        ColorPickerFragment fragment = new ColorPickerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ColorPickerFragment() {
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
        View v =  inflater.inflate(R.layout.fragment_color_picker, container, false);
        Button b = (Button)v.findViewById(R.id.addColorButton);
        b.setOnClickListener(this);

        b = (Button)v.findViewById(R.id.cancelColorButton);
        b.setOnClickListener(this);

        // set behavior of color wheel
        picker = (ColorPicker)v.findViewById(R.id.picker);
        picker.addSVBar((SVBar) v.findViewById(R.id.svbar));
        picker.setShowOldCenterColor(false);
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
    public void onClick(View v) {
        if(mListener!=null){
            Bundle b = new Bundle();
            switch(v.getId()){
                case R.id.addColorButton:
                        b.putInt("color", picker.getColor());
                        mListener.handleDialogResult(getTargetRequestCode(), Activity.RESULT_OK,  b);

                    break;
                case R.id.cancelColorButton:
                    mListener.handleDialogResult(getTargetRequestCode(), Activity.RESULT_CANCELED, b);
                    break;
            }
        }
        dismiss();

    }

}
