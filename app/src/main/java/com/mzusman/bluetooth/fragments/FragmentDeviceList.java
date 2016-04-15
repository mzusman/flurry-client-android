package com.mzusman.bluetooth.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mzusman.bluetooth.R;
import com.mzusman.bluetooth.utils.Constants;

import java.util.ArrayList;

/**
 * Created on 04/15/2016.
 */
public class FragmentDeviceList extends Fragment {

	ArrayAdapter<String> adapter;

	BTConnector btConnector ;
//	WIFIConnector wifiConnector;


	@Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
												 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.device_list_frag, container, false);
		((AppCompatActivity) getActivity()).getSupportActionBar()
										   .setTitle("Select Device");//change toolbar title


		final ListView listOfDevices = (ListView) view.findViewById(R.id.device_list);

		final ArrayList<String> devicesArrayList = new ArrayList<>();

		adapter = new ArrayAdapter<String>(getActivity(), R.layout.device_view);


		listOfDevices.setAdapter(adapter);


		listOfDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				String   deviceAdress = adapter.getItem(position);
				Fragment details      = new FragmentDetailsList();
				Bundle   bundle       = new Bundle();
				bundle.putString("DEVICE", deviceAdress);
				details.setArguments(bundle);
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_container, details);
				transaction.commit();
			}
		});

		return view;
	}





	class BTConnector {
		private BluetoothAdapter bluetoothAdapter;

		public BTConnector() {
			bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			if (bluetoothAdapter == null) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					@Override public void onClick(DialogInterface dialog, int which) {
						getActivity().finish();

					}
				});
				builder.setMessage("not supported");
				builder.show();

			}
			while (!bluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, Constants.REQUEST_ENABLE_BT);
			}
//
//		final Set<BluetoothDevice> bluetoothDevices = bluetoothAdapter.getBondedDevices();
//		if (bluetoothDevices.size() > 0) {
//			for (BluetoothDevice device : bluetoothDevices) {
//				adapter.add(device.getName() + "\n" + device.getAddress());
//				devices.add(device.getAddress());
//			}
//		}


		}


	}
}
