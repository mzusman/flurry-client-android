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
public class FragmentChooseManager extends Fragment{

	Button btButton;
	Button wifiButton;





	@Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
												 Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.choose_fragment,container,false);

		btButton = (Button) v.findViewById(R.id.bt);
		wifiButton = (Button) v.findViewById(R.id.wifi);



		return v;
	}
}
