package com.mzusman.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
	String deviceAdress      = null;


	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final ArrayList<String> devices  = new ArrayList<>();
		ArrayAdapter<String>    adapter  = new ArrayAdapter<String>(this, R.layout.device_name);
		final ListView          listView = (ListView) findViewById(R.id.paired);
		setTitle("Select Device");
//		listView.setAdapter(adapter);

//		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//		if (bluetoothAdapter == null) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setMessage("not supported");
//			builder.show();

//		}
//		if (!bluetoothAdapter.isEnabled()) {
//			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//			startActivityForResult(enableBtIntent, Constants.REQUEST_ENABLE_BT);
//
//		}
//
//		final Set<BluetoothDevice> bluetoothDevices = bluetoothAdapter.getBondedDevices();
//		if (bluetoothDevices.size() > 0) {
//			for (BluetoothDevice device : bluetoothDevices) {
//				adapter.add(device.getName() + "\n" + device.getAddress());
//				devices.add(device.getAddress());
//			}
//		}
		adapter.add("asdasd");
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//				deviceAdress = devices.get(position);
				Intent intent = new Intent(MainActivity.this , DetailsActivity.class);
//				intent.putExtra("address",deviceAdress);
				startActivity(intent);

			}
		});



	}
}
