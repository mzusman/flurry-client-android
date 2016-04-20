package com.mzusman.bluetooth.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mzusman.bluetooth.R;

import info.hoang8f.widget.FButton;

/*
 * Class : FragmentChooseMangager.
 * Created by mzusman - morzusman@gmail.com on 4/15/16.
 */
public class FragmentChooseManager extends DialogFragment {

    FButton btButton;
    FButton wifiButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.choose_fragment, container, false);

        btButton = (FButton) v.findViewById(R.id.bt);
        wifiButton = (FButton) v.findViewById(R.id.wifi);


        return v;
    }
}
