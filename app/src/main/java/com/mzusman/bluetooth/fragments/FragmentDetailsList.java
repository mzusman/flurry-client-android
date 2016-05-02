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
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.mzusman.bluetooth.R;
import com.mzusman.bluetooth.model.Manager;
import com.mzusman.bluetooth.model.Managers.GpsManager;
import com.mzusman.bluetooth.model.Managers.WifiManager;
import com.mzusman.bluetooth.model.Model;
import com.mzusman.bluetooth.utils.Constants;
import com.mzusman.bluetooth.utils.DetailsAdapter;
import com.mzusman.bluetooth.utils.DetailsThread;

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

    private DetailsThread detailsTask;

    private SpotsDialog dialog;

    PowerManager.WakeLock wakeLock;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_details, container, false);
        this.activity = getActivity();

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

        listView = (ListView) view.findViewById(R.id.details);
        detailsAdapter = new DetailsAdapter(activity);
        listView.setAdapter(detailsAdapter);

        Button stopBtn = (Button) view.findViewById(R.id.stop_btn);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wakeLock.release();
                if (detailsTask.isAlive())
                    detailsTask.stopRunning();
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Disconnect from the device connection and get back into 3G/4G services or any other Wifi services." +
                        "Click 'Send' as soon as you're connected").setPositiveButton("Send", new DialogInterface.OnClickListener() {
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
        PowerManager powerManager = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, Constants.WAKE_LOG_TAG);
        wakeLock.acquire();

        return view;

    }

    /**
     * initate the location service - checks if the user specified a denial of the location services
     * and notify him about that . - > get the location listener ready
     */
    private void locationInit() {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new GpsManager();

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
                Constants.GPS_MIN_TIME,
                Constants.GPS_MIN_DISTANCE,
                locationListener);

        //sets the manager
        Model.getInstance().setGpsManager((GpsManager) locationListener);

    }

    /**
     * thread for recieving the information
     */
    private void initThread() {
        if (detailsTask == null)
            detailsTask = new DetailsThread(locationListener, getActivity(), listView);
        detailsTask.start();
    }

    private String loadFromFile() throws IOException {
        showDialog("Sending...");
        File file = activity.getFileStreamPath(Constants.FILE_DATA);
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fileInputStream.read(data);
        fileInputStream.close();

        String str = new String(data, "UTF-8");
        return str;

    }

    private void showDialog(String message) {
        dialog = new SpotsDialog(getActivity(), message);
        dialog.show();
    }

    private void dismissDialog() {
        if (dialog == null)
            return;
        if (dialog.isShowing()) {

            dialog.dismiss();
            dialog = null;
        }
    }


    private void sendRemote(final int driverID) throws IOException {

        String data = loadFromFile();
        Model.getInstance().getNetworkManager().sendData(driverID, data, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                dismissDialog();
                if (response.isSuccessful()) {
                    onSentSuccess();
                } else errorEscape();

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                dismissDialog();
                errorEscape();
            }
        });
    }

    void errorEscape() {
        AlertDialog.Builder error = new AlertDialog.Builder(getActivity());
        error.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
            }
        }).setMessage("Error").show();
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


}
