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
import android.support.v7.app.AlertDialog;
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
import com.mzusman.bluetooth.model.GPSManager;
import com.mzusman.bluetooth.model.Manager;
import com.mzusman.bluetooth.model.Model;
import com.mzusman.bluetooth.model.NetworkManager;
import com.mzusman.bluetooth.model.WifiManager;
import com.mzusman.bluetooth.utils.Constants;
import com.mzusman.bluetooth.utils.DetailsAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by amitmu on 04/15/2016.
 */
public class FragmentDetailsList extends Fragment {

    private int userID;
    private ArrayList<String> arrayList;
    private ListView listView;
    private Activity activity;
    private DetailsAdapter detailsAdapter;
    private LocationListener locationListener;
    private JsonWriter jsonWriter;
    private DetailsTask detailsTask;
    private FileOutputStream fileOutputStream;
    String manager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.activity_details, container, false);
        this.activity = getActivity();


        /**
         * Build the factory out side of the manager class
         */
        userID = getArguments().getInt(Constants.USER_ID_TAG);
        manager = getArguments().getString(Constants.MANAGER_TAG);
        if (manager.equals(Constants.WIFI_TAG)) {
            Model.getInstance().setManager(new WifiManager(new Manager.Factory() {
                @Override
                public void setCommandsFactory(HashMap<String, ObdCommand> commandsFactory) {
                    commandsFactory.put(Constants.REQUEST_SPEED_READING, new SpeedCommand());
                    commandsFactory.put(Constants.REQUEST_RPM_READING, new RPMCommand());
                }
            }), Constants.WIFI_ADDRESS);

        }

        Log.d(Constants.IO_TAG, "onCreateView: UserID = " + userID);

        listView = (ListView) view.findViewById(R.id.details);
        detailsAdapter = new DetailsAdapter(activity);
        listView.setAdapter(detailsAdapter);


        locationInit();
        initThread();

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
        detailsTask = new DetailsTask(locationListener, getActivity(), listView);
        detailsTask.start();

    }

    private synchronized void sendRemote(int driverID) throws IOException {
        File file = activity.getFileStreamPath("data2.json");
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fileInputStream.read(data);
        fileInputStream.close();
        String str = new String(data, "UTF-8");
        NetworkManager networkManager = new NetworkManager();
        networkManager.connect();
        Log.d(Constants.IO_TAG, "endJsonWrite:" + str);
    }


    @Override
    public void onPause() {
        detailsTask.stopRunning();
        super.onPause();
    }


    public void setDetailsAdapter(DetailsAdapter detailsAdapter) {
        this.detailsAdapter = detailsAdapter;
    }
}
