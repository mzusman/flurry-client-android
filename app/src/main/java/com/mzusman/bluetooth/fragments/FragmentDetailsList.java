package com.mzusman.bluetooth.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
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
import com.mzusman.bluetooth.R;
import com.mzusman.bluetooth.model.DriverService;
import com.mzusman.bluetooth.model.GPSManager;
import com.mzusman.bluetooth.model.Manager;
import com.mzusman.bluetooth.model.Model;
import com.mzusman.bluetooth.model.NetworkManager;
import com.mzusman.bluetooth.model.WifiManager;
import com.mzusman.bluetooth.utils.Constants;
import com.mzusman.bluetooth.utils.DetailsAdapter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Path;

/**
 * Created by amitmu on 04/15/2016.
 */
public class FragmentDetailsList extends Fragment {

    private Thread thread;
    private ArrayList<String> arrayList;
    private ListView listView;
    private Activity activity;
    private DetailsAdapter detailsAdapter;
    private LocationListener locationListener;
    private JsonWriter jsonWriter;
    private FileOutputStream fileOutputStream;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.activity_details, container, false);

        /**
         * set action bar title
         */
        activity = getActivity();
        ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle("Loading");


        /**
         * Build the factory out side of the manager class
         */
        String request = getArguments().getString(Constants.MANAGER_TAG);
        if (request.equals(Constants.WIFI_TAG)) {
            Model.getInstance().setManager(new WifiManager(new Manager.Factory() {
                @Override
                public void setCommandsFactory(HashMap<String, ObdCommand> commandsFactory) {
                    commandsFactory.put(Constants.REQUEST_SPEED_READING, new SpeedCommand());
                    commandsFactory.put(Constants.REQUEST_RPM_READING, new RPMCommand());
//                    commandsFactory.put(Constants.REQUEST_THR_READING,
//                            new ThrottlePositionCommand());
                }
            }), Constants.WIFI_ADDRESS);

        }


        listView = (ListView) view.findViewById(R.id.details);
        detailsAdapter = new DetailsAdapter(activity);
        listView.setAdapter(detailsAdapter);


        locationInit();
        initThread();
        thread.start();
        if (thread.isAlive()) {
            actionBar.setTitle("Running");
        }

        return view;

    }

    /**
     * initate the location service - checks if the user specified a denial of the location services
     * and notify him about that . - > get the location listener ready
     */
    private void locationInit() {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new GPSManager();
        //check if there are permissions -
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().finish();

                }
            }).setMessage("Please Enable Location Services").show();

        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                5000,
                10,
                locationListener);

    }

    /**
     * thread for recieving the information
     */
    private void initThread() {
        thread = new Thread(new Runnable() {
            ArrayList<String> readings;

            @Override
            public void run() {
                try {
                    initJsonWriting();//initates the json reading

                    Model.getInstance().getManager().connect(Constants.WIFI_ADDRESS);
                    readings = Model.getInstance().getReading();//assigned to the Manager .

                    while (!Thread.currentThread().isInterrupted()) {

                        readings = Model.getInstance().getReading();//assigned to the Manager .
                        readings.add(((GPSManager) locationListener).getReading(Constants.GPS_TAG));

                        writeToJson(readings);

                        listView.post(new Runnable() {
                            @Override
                            public void run() {
                                detailsAdapter.setArray(readings);
                                detailsAdapter.notifyDataSetChanged();
                            }
                        });
                        Thread.sleep(500);
                    }

                    endJsonWrite();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    try {
                        endJsonWrite();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }


    @Override
    public void onPause() {
//        NetworkManager networkManager = new NetworkManager();
//        networkManager.connect();
//        networkManager.sendData();
        this.thread.interrupt();
        try {
            this.thread.join();
        } catch (InterruptedException e) {
            Log.d(Constants.RUN_TAG, "onPause: " + Constants.RUN_TAG);
            e.printStackTrace();
        }
        super.onPause();
    }


    private void initJsonWriting() throws IOException {
        fileOutputStream = activity.openFileOutput("data2.json", Context.MODE_PRIVATE);
        jsonWriter = new JsonWriter(new OutputStreamWriter(fileOutputStream, "UTF-8"));
        jsonWriter.beginArray();
    }


    private void writeToJson(ArrayList<String> readings) throws IOException {

        jsonWriter.beginObject();

//        String[] read = str.split(",");

        jsonWriter.name("time").value(readings.get(0).split(",")[1]);
        jsonWriter.name("speed").value(readings.get(0).split(",")[2]);
        jsonWriter.name("rpm").value(readings.get(1).split(",")[2]);
        jsonWriter.name("gps");
        jsonWriter.beginObject();
        jsonWriter.name("lat").value(readings.get(2).split(",")[1]);
        jsonWriter.name("lon").value(readings.get(2).split(",")[2]);
        jsonWriter.endObject();
        jsonWriter.endObject();
    }


    private void endJsonWrite() throws IOException {
        jsonWriter.endArray();
        jsonWriter.close();
        this.fileOutputStream.close();
        File file= activity.getFileStreamPath("data2.json");
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fileInputStream.read(data);
        fileInputStream.close();
        String str = new String(data, "UTF-8");
        NetworkManager networkManager = new NetworkManager();
        networkManager.connect();
        networkManager.sendData(8,str);
        Log.d(Constants.IO_TAG, "endJsonWrite:" +str);


    }


    public void setDetailsAdapter(DetailsAdapter detailsAdapter) {
        this.detailsAdapter = detailsAdapter;
    }
}
