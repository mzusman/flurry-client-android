package com.mzusman.bluetooth;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/*
 * Class : FragmentChooseMangager.
 * Created by mzusman - morzusman@gmail.com on 4/15/16.
 */
public class FragmentChooseManager extends Fragment {

	Button btButton;
	Button wifiButton;


	Fragment fragment = new FragmentDeviceList();

	/**
	 *
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 * @return
	 */
	@Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
												 Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.choose_fragment, container, false);

		btButton = (Button) v.findViewById(R.id.bt);
		wifiButton = (Button) v.findViewById(R.id.wifi);

		btButton.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				Bundle btBundle = new Bundle();
				btBundle.putString(Constants.MANAGER_TAG, Constants.BT_TAG);
				fragment.setArguments(btBundle);
				getActivity().getFragmentManager().beginTransaction()
							 .replace(R.id.fragment_container, fragment, Constants.DETAILS_TAG);

			}
		});

		wifiButton.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {

				Bundle wifiBundle = new Bundle();
				wifiBundle.putString(Constants.MANAGER_TAG, Constants.WIFI_TAG);
				fragment.setArguments(wifiBundle);
				getActivity().getFragmentManager().beginTransaction()
							 .replace(R.id.fragment_container, new FragmentDeviceList(),
									  Constants.DETAILS_TAG);

			}
		});

		return v;
	}
}
