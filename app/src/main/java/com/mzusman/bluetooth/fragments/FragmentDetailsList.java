package com.mzusman.bluetooth.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.JsonWriter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mzusman.bluetooth.R;
import com.mzusman.bluetooth.utils.DetailsAdapter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by amitmu on 04/15/2016.
 */
public class FragmentDetailsList extends Fragment {

	ArrayList<String> arrayList;
	ListView          deviceDetails;
	DetailsAdapter    deviceAdapter;

	@Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
												 Bundle savedInstanceState) {

		View   view = inflater.inflate(R.layout.activity_details, container, false);
		String s    = getArguments().getString("DEVICE");
		deviceDetails = (ListView) view.findViewById(R.id.details);
		arrayList = new ArrayList<>();
		deviceAdapter = new DetailsAdapter(arrayList, getActivity());
		deviceDetails.setAdapter(deviceAdapter);

		return view;
	}





	public void writeToJson(JsonWriter jsonWriter, String string) throws IOException {


		jsonWriter.beginObject();
		jsonWriter.name("time").value(string.split(",")[0]);
		jsonWriter.name("value").value(string.split(",")[1]);
		jsonWriter.endObject();


	}

}
