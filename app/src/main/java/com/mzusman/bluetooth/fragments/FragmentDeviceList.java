package com.mzusman.bluetooth.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
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
import android.widget.ListView;

import com.mzusman.bluetooth.R;
import com.mzusman.bluetooth.utils.Connector;
import com.mzusman.bluetooth.utils.Constants;
import com.mzusman.bluetooth.utils.DevicesAdapter;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created on 04/15/2016.
 */
public class FragmentDeviceList extends Fragment {

	DevicesAdapter devicesAdapter;

	BTConnector btConnector;
//	WIFIConnector wifiConnector;

	ArrayList<String> devicesArrayList = new ArrayList<>();

	@Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
												 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.device_list_frag, container, false);
		((AppCompatActivity) getActivity()).getSupportActionBar()
										   .setTitle("Select Device");//change toolbar title


		final ListView listOfDevices = (ListView) view.findViewById(R.id.device_list);


		devicesAdapter = new DevicesAdapter(devicesArrayList, getActivity());


		listOfDevices.setAdapter(devicesAdapter);


		listOfDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				String   deviceAdress = devicesAdapter.getDeviceAddress(position);
				Fragment details      = new FragmentDetailsList();
				Bundle   bundle       = new Bundle();
				bundle.putString(Constants.DEVICE_TAG, deviceAdress);

				details.setArguments(bundle);

				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_container, details);
				transaction.commit();
			}
		});

		return view;
	}


	class BTConnector implements Connector{
		private BluetoothAdapter bluetoothAdapter;

		@Override public void initateConnection() {
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

		final Set<BluetoothDevice> bluetoothDevices = bluetoothAdapter.getBondedDevices();
		if (bluetoothDevices.size() > 0) {
			for (BluetoothDevice device : bluetoothDevices) {
				FragmentDeviceList.this.devicesArrayList.add(device.getName() + "," + device.getAddress());
				FragmentDeviceList.this.devicesAdapter.notifyDataSetChanged();
			}
		}


		}
	}



	class WIFIConnector implements Connector{


		@Override public void initateConnection() {

		}
	}
}
