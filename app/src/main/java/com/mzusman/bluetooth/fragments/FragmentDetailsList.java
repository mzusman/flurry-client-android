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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.mzusman.bluetooth.R;
import com.mzusman.bluetooth.model.GPSManager;
import com.mzusman.bluetooth.model.Manager;
import com.mzusman.bluetooth.model.Model;
import com.mzusman.bluetooth.model.WifiManager;
import com.mzusman.bluetooth.utils.Constants;
import com.mzusman.bluetooth.utils.DetailsAdapter;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by amitmu on 04/15/2016.
 */
public class FragmentDetailsList extends Fragment {

    private int driverID;
    private ListView listView;
    private Activity activity;
    private DetailsAdapter detailsAdapter;
    private LocationListener locationListener;
    private DetailsTask detailsTask;


    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_details, container, false);
        this.activity = getActivity();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Getting Prepared");
        /**
         * Build the factory out side of the manager class
         */
        driverID = getArguments().getInt(Constants.USER_ID_TAG);
        String manager = getArguments().getString(Constants.MANAGER_TAG);
        if (manager.equals(Constants.WIFI_TAG)) {
            Model.getInstance().setManager(new WifiManager(new Manager.Factory() {
                @Override
                public void setCommandsFactory(HashMap<String, ObdCommand> commandsFactory) {
                    commandsFactory.put(Constants.REQUEST_SPEED_READING, new SpeedCommand());
                    commandsFactory.put(Constants.REQUEST_RPM_READING, new RPMCommand());
                }
            }), Constants.WIFI_ADDRESS);

        }

        Log.d(Constants.IO_TAG, "onCreateView: UserID = " + driverID);

        listView = (ListView) view.findViewById(R.id.details);
        detailsAdapter = new DetailsAdapter(activity);
        listView.setAdapter(detailsAdapter);

        Button stopBtn = (Button) view.findViewById(R.id.stop_btn);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (detailsTask.isAlive())
                    detailsTask.stopRunning();
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("In order to send the information to us , we need you to disconnect" +
                        "from the device connection and connect back to 3G service or any other Wifi services." +
                        "Click 'Send' as long as you're ready!").setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            sendRemote(driverID);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).show();
            }
        });

        locationInit();
        initThread();

        return view;

    }

    private void errorEscape() {
        AlertDialog.Builder error = new AlertDialog.Builder(activity);
        error.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
            }
        }).setMessage("Error").show();

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

        //sets the manager
        Model.getInstance().setGpsManager((GPSManager) locationListener);

    }

    /**
     * thread for recieving the information
     */
    private void initThread() {
        detailsTask = new DetailsTask(locationListener, getActivity(), listView);
        detailsTask.start();

    }

    private void sendRemote(final int driverID) throws IOException {
        final SpotsDialog dialog = new SpotsDialog(getActivity(), "Sending..");
        dialog.show();
        File file = activity.getFileStreamPath("data2.json");
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fileInputStream.read(data);
        fileInputStream.close();
        String str = new String(data, "UTF-8");

        Model.getInstance().getNetworkManager().sendData(driverID, str, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                dialog.dismiss();
                Log.d(Constants.IO_TAG, "onResponse2222: " + response.code());
                if (response.isSuccessful()) {
                    onSentSuccess();
                } else errorEscape();

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

                dialog.dismiss();
                errorEscape();
            }
        });
//        Log.d(Constants.IO_TAG, "endJsonWrite:" + str);
    }

    private void onSentSuccess() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Information has been Sent. \nThank You!")
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new FragmentProfile(),
                                        Constants.DETAILS_TAG).commit();
                    }
                }).setCancelable(false).show();

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
