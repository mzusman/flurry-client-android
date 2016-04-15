package com.mzusman.bluetooth;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.JsonWriter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by amitmu on 04/15/2016.
 */
public class FragmentDetailsList extends Fragment {

    ArrayList<String> arrayList;
    Thread thread = null;
    ListView       deviceDetails;
    DetailsAdapter detailsAdapter;
    boolean run = true;
    //String           deviceAddress;
    //BluetoothAdapter bluetoothAdapter;
    //BluetoothDevice device;
    UUID uuid = UUID.fromString("667d60d3-981e-41c8-befc-ba931ebaa385");
    JsonWriter jsonWriter;
    FileOutputStream fileOutputStream;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_details, container, false);
        String s = getArguments().getString("DEVICE");
        deviceDetails = (ListView) view.findViewById(R.id.details);
        arrayList = new ArrayList<>();
        detailsAdapter = new DetailsAdapter(arrayList, getActivity());
        deviceDetails.setAdapter(detailsAdapter);



        Thread thread = new Thread(new Runnable() {
            String timeStr;

            @Override
            public void run() {

                try {

                    fileOutputStream = getActivity().openFileOutput("js.json", Context.MODE_PRIVATE);
                    jsonWriter = new JsonWriter(new OutputStreamWriter(fileOutputStream));
                    jsonWriter.beginArray();

                    long tmp = System.currentTimeMillis();
                    arrayList.add("0" + "," + "hello");
                    while (run) {
                        long time = System.currentTimeMillis();

                        timeStr = Long.toString(time) + "," + "hello";
                        arrayList.set(0, timeStr);
                        if (time != tmp) {
                            tmp = time;
                            writeToJson(jsonWriter, timeStr);
                        }

                        deviceDetails.post(new Runnable() {
                            @Override
                            public void run() {
                                deviceDetails.setAdapter(detailsAdapter);
                            }

                        });

                    }
                    Log.i("Thread", "json: Dead");
                    jsonWriter.endArray();
                    jsonWriter.close();
                    Log.i("Thread", "file: Dead");
                    fileOutputStream.close();


                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        }

        );
        thread.start();
        return view;
    }
    public void writeToJson(JsonWriter jsonWriter, String string) throws IOException {


        jsonWriter.beginObject();
        jsonWriter.name("time").value(string.split(",")[0]);
        jsonWriter.name("value").value(string.split(",")[1]);
        jsonWriter.endObject();


    }

}
