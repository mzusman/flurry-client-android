package com.mzusman.bluetooth;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
	String deviceAdress      = null;


	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getSupportActionBar().setTitle("Welcome");

        Fragment DeviceList=new FragmentChooseManager();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, DeviceList);
        transaction.commit();


		//final ListView          listView = (ListView) findViewById(R.id.paired);

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




	}
}
