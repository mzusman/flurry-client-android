package com.mzusman.bluetooth.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mzusman.bluetooth.R;
import com.mzusman.bluetooth.utils.Constants;

import info.hoang8f.widget.FButton;

/*
 * Class : FragmentChooseMangager.
 * Created by mzusman - morzusman@gmail.com on 4/15/16.
 */
public class FragmentChooseManager extends Fragment {

	FButton btButton;
	FButton wifiButton;


	Fragment fragment = new FragmentDeviceList();

	@Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
												 Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.choose_fragment, container, false);

		btButton = (FButton) v.findViewById(R.id.bt);
		wifiButton = (FButton) v.findViewById(R.id.wifi);

		btButton.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				Bundle btBundle = new Bundle();
				btBundle.putString(Constants.MANAGER_TAG,
								   Constants.BT_TAG); // making archive for the next fragment
				// to know that we clicked on the BT button
				fragment.setArguments(btBundle);
				getActivity().getFragmentManager().beginTransaction()
							 .replace(R.id.fragment_container, fragment, Constants.DETAILS_TAG).commit();

			}
		});

		wifiButton.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {

				Bundle wifiBundle = new Bundle();
				fragment = new FragmentDetailsList();
				wifiBundle.putString(Constants.MANAGER_TAG, Constants.WIFI_TAG);
				fragment.setArguments(wifiBundle);
				getActivity().getFragmentManager().beginTransaction()
							 .replace(R.id.fragment_container, fragment,
									  Constants.DETAILS_TAG).commit();

			}
		});

		return v;
	}
}
