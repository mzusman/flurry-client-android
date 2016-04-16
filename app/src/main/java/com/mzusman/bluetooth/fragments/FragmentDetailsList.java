package com.mzusman.bluetooth.fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonWriter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.engine.ThrottlePositionCommand;
import com.mzusman.bluetooth.R;
import com.mzusman.bluetooth.model.GPSManager;
import com.mzusman.bluetooth.model.Manager;
import com.mzusman.bluetooth.model.Model;
import com.mzusman.bluetooth.model.WifiManager;
import com.mzusman.bluetooth.utils.Constants;
import com.mzusman.bluetooth.utils.DetailsAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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


		View view = inflater.inflate(R.layout.activity_details, container, false);
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Read Parameters");
		String request = getArguments().getString(Constants.MANAGER_TAG);
		if (request.equals(Constants.WIFI_TAG)) {
			Model.getInstance().setManager(new WifiManager(new Manager.Factory() {
				@Override
				public void setCommandsFactory(HashMap<String, ObdCommand> commandsFactory) {
					commandsFactory.put(Constants.REQUEST_SPEED_READING, new SpeedCommand());
					commandsFactory.put(Constants.REQUEST_RPM_READING, new RPMCommand());
					commandsFactory
							.put(Constants.REQUEST_THR_READING, new ThrottlePositionCommand());
				}
			}), Constants.WIFI_ADDRESS);

		}
		//location manager

		final LocationManager locationManager =
				(LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		final LocationListener locationListener = new GPSManager();

		if (ActivityCompat
					.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
			PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
																					Manifest.permission.ACCESS_COARSE_LOCATION) !=
												 PackageManager.PERMISSION_GRANTED) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which) {
					getActivity().finish();

				}
			}).setMessage("Please Enable Location Services").show();

		}

		locationManager
				.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);





		listView = (ListView) view.findViewById(R.id.details);
		arrayList = new ArrayList<>();
		detailsAdapter = new DetailsAdapter(arrayList, getActivity());
		listView.setAdapter(detailsAdapter);

		new Thread(new Runnable() {
			@Override public void run() {
				Log.d(Constants.WIFI_TAG, "run: READING");
				Model.getInstance().getManager().connect(Constants.WIFI_ADDRESS);
				arrayList.add("0" + "," + "0" + "," + "0");
				arrayList.add("0" + "," + "0" + "," + "0");
				arrayList.add("0" + "," + "0" + "," + "0");
				arrayList.add("0" + "," + "0" + "," + "0");
				while (run) {
					Log.d(Constants.WIFI_TAG, "run: READING");
					List<String> strings = Model.getInstance().getReading();
					arrayList.set(0, strings.get(0));
					arrayList.set(1, strings.get(1));
					arrayList.set(2, strings.get(2));
					arrayList.set(3,((GPSManager)locationListener).getReading(Constants.GPS_TAG));

					listView.post(new Runnable() {
						@Override public void run() {
							detailsAdapter.notifyDataSetChanged();
						}
					});
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
