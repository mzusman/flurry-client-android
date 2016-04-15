package com.mzusman.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.util.JsonWriter;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.engine.ThrottlePositionCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.UUID;

/*
 * Class : DetailsActivity.
 * Created by mzusman - morzusman@gmail.com on 4/11/16.
 */
public class DetailsActivity extends Activity {
	ArrayList<String> arrayList;
	Thread thread = null;
	ListView       listView;
	DetailsAdapter detailsAdapter;
	boolean run = true;
	String           deviceAddress;
	BluetoothAdapter bluetoothAdapter;
	BluetoothDevice  device;


	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);

//		thread = new Thread(new Runnable() {
//			String timeStr;
//
//			@Override public void run() {
//
//				try {
//					fileOutputStream = openFileOutput("js.json", Context.MODE_PRIVATE);

//					jsonWriter = new JsonWriter(new OutputStreamWriter(fileOutputStream));
//					jsonWriter.beginArray();
//
//					arrayList.add("0" + "," + "hello");
//					while (run) {
//						long time = System.currentTimeMillis();
//
//						timeStr = Long.toString(time) + "," + "hello";
//						arrayList.set(0, timeStr);
//						if (time != tmp) {
//							tmp = time;
//							writeToJson(jsonWriter, timeStr);
//						}
//
//						listView.post(new Runnable() {
//							@Override public void run() {
//								listView.setAdapter(detailsAdapter);
//							}
//
//						});
//
//					}
//					Log.i("Thread", "json: Dead");
//					jsonWriter.endArray();
//					jsonWriter.close();
//					Log.i("Thread", "file: Dead");
//					fileOutputStream.close();
//
//
//
//				}
//				catch (FileNotFoundException e1) {
//					e1.printStackTrace();
//				}
//				catch (IOException e1) {
//					e1.printStackTrace();
//				}
//
//			}
//		}
//
//		);
//		thread.start();
//
//
	}

	@Override protected void onPause() {
		super.onPause();
		Log.i("OP", "onPause: enter");


		try {
			run = false;
			thread.join();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		if (!thread.isAlive()) Log.i("OP", "onPause: Dead");
	}

//	@Override protected void onDestroy() {
//		super.onDestroy();
//		Log.i("OD", "onDestroy: enter");
//
//		if (!thread.isInterrupted()) thread.interrupt();
//		try {
//			if (thread.isInterrupted()) {
//				Log.i("TH", "onStop: interrupted");
//				thread.join();
//			}
//		}
//		catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Override protected void onStop() {
//		super.onStop();
//		Log.i("OS", "onStop: enter");
//
//		if (!thread.isInterrupted()) thread.interrupt();
//		if (thread.isInterrupted()) {
//			Log.i("TH", "onStop: interrupted");
//			try {
//				thread.join();
//			}
//			catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//	}


	public void writeToJson(JsonWriter jsonWriter, String string) throws IOException {


		jsonWriter.beginObject();
		jsonWriter.name("time").value(string.split(",")[0]);
		jsonWriter.name("value").value(string.split(",")[1]);
		jsonWriter.endObject();


	}
}
