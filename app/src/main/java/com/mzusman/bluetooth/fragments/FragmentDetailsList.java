package com.mzusman.bluetooth.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonWriter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.SpeedCommand;
import com.mzusman.bluetooth.R;
import com.mzusman.bluetooth.model.Manager;
import com.mzusman.bluetooth.model.Model;
import com.mzusman.bluetooth.model.WifiManager;
import com.mzusman.bluetooth.utils.Constants;
import com.mzusman.bluetooth.utils.DetailsAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by amitmu on 04/15/2016.
 */
public class FragmentDetailsList extends Fragment {

	boolean run = true;
	ArrayList<String> arrayList;
	ListView          listView;
	DetailsAdapter    detailsAdapter;


	@Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
												 Bundle savedInstanceState) {


		View              view = inflater.inflate(R.layout.activity_details, container, false);
		((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Read Parameters");
		String request = getArguments().getString(Constants.MANAGER_TAG);
		if(request.equals(Constants.WIFI_TAG)){
			Model.getInstance().setManager(new WifiManager(new Manager.Factory() {
				@Override
				public void setCommandsFactory(HashMap<String, ObdCommand> commandsFactory) {
					commandsFactory.put(Constants.REQUEST_SPEED_READING,new SpeedCommand());
				}
			}), Constants.WIFI_ADDRESS);
		}

		listView = (ListView) view.findViewById(R.id.details);
		arrayList = new ArrayList<>();
		detailsAdapter = new DetailsAdapter(arrayList, getActivity());
		listView.setAdapter(detailsAdapter);

		new Thread(new Runnable() {
			@Override public void run() {
				Log.d(Constants.WIFI_TAG, "run: READING");
				Model.getInstance().getManager().connect(Constants.WIFI_ADDRESS);
				while (run){
					Log.d(Constants.WIFI_TAG, "run: READING");
					String string= Model.getInstance().getRead(Constants.REQUEST_SPEED_READING);
					arrayList.add(string);
					detailsAdapter.notifyDataSetChanged();
				}
			}
		}).start();
		return view;
	}

	@Override public void onPause() {
		super.onPause();
		run = false;
	}

	public void writeToJson(JsonWriter jsonWriter, String string) throws IOException {


		jsonWriter.beginObject();
		jsonWriter.name("time").value(string.split(",")[0]);
		jsonWriter.name("value").value(string.split(",")[1]);
		jsonWriter.endObject();


	}

}
