package com.mzusman.bluetooth.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.mzusman.bluetooth.R;
import com.mzusman.bluetooth.commands.ObdCommand;
import com.mzusman.bluetooth.enums.AvailableCommandNames;
import com.mzusman.bluetooth.model.Managers.GpsManager;
import com.mzusman.bluetooth.model.Model;
import com.mzusman.bluetooth.utils.Constants;
import com.mzusman.bluetooth.utils.adapters.DetailsAdapter;
import com.mzusman.bluetooth.utils.logger.Log4jHelper;
import com.mzusman.bluetooth.utils.thread.DetailsThread;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentDetailsList extends Fragment {

    private int driverID;

    Logger log = Log4jHelper.getLogger("DetailsListFragment");

    private ListView listView;

    private Activity activity;

    private DetailsThread detailsTask;

    private SpotsDialog dialog;

    private TextView timeView;


    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        this.activity = getActivity();
        setHasOptionsMenu(true);

        /**
         * Build the factory out side of the manager class
         */
        driverID = getArguments().getInt(Constants.USER_ID_TAG);
        log.debug("onResponse: id:" + driverID);
        String manager = getArguments().getString(Constants.MANAGER_TAG);
        Model.getInstance().createNewManager(manager);

        listView = (ListView) view.findViewById(R.id.details);
        DetailsAdapter detailsAdapter = new DetailsAdapter(activity);
        listView.setAdapter(detailsAdapter);
        timeView = (TextView) view.findViewById(R.id.time);
        locationInit();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!menu.hasVisibleItems()) {
            MenuInflater menuInflater = getActivity().getMenuInflater();
            menuInflater.inflate(R.menu.details_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        stopRunning();
        return true;
    }

    private void stopRunning() {
        detailsTask.stopRunning(new DetailsThread.Callback() {
            @Override
            public void ThreadDidStop() {
                log.debug("stop running");
                int message = R.string.dsc_wifi_msg;
                //check if connected to the wifi
                WifiManager wifi = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
                wifi.setWifiEnabled(false);
                if (wifi.isWifiEnabled())
                    message = R.string.dsc_wifi_msg_con;
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(message).setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendRemote(driverID);
                    }
                }).setCancelable(false).show();
            }
        });
    }

    void sendAgain() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setCancelable(false).setMessage("Cannot connect to the cloud, Please try again.").setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendRemote(driverID);
            } }) .setNegativeButton("Cencel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getActivity().finish();
                    }
                }).show();
    }

    /**
     * initate the location service - checks if the user specified a denial of the location services
     * and notify him about that . - > get the location listener ready
     */
    private void locationInit() {

        //sets the manager
        if (!Model.getInstance().isGpsEnabled()) {
            buildAlertMessageNoGps();
        } else initThread();
    }


    private void buildAlertMessageNoGps() {
        new AlertDialog.Builder(getActivity()).
                setMessage("Your GPS seems to be disabled, please enable it")
                .setCancelable(false)
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new FragmentProfile(),
                                        Constants.DETAILS_TAG).commit();
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new FragmentProfile(),
                                Constants.DETAILS_TAG).commit();
            }
        }).create().show();
    }

    public class CallBack {
        public void onStop() {
            stopRunning();
        }
    }

    /**
     * thread for recieving the information
     */
    private void initThread() {
        CallBack callBack = new CallBack();
        if (detailsTask == null)
            detailsTask = new DetailsThread(callBack, listView, getActivity(), timeView);
        log.debug("start detailsThread");
        detailsTask.start();
    }

    private String loadFromFile() throws IOException {
        showDialog("Sending...");
        File file = activity.getFileStreamPath(Constants.FILE_DATA);
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fileInputStream.read(data);
        fileInputStream.close();
        return new String(data, "UTF-8");
    }

    private void showDialog(String message) {
        dialog = new SpotsDialog(getActivity(), message);
        dialog.setCancelable(false);
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

    private void sendRemote(int driverID) {
        String data = null;
        try {
            data = loadFromFile();
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
        Model.getInstance().getNetworkManager().sendData(driverID, data, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                log.debug("send data success");
                dismissDialog();
                onSentSuccess();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                log.debug("send data fail");
                dismissDialog();
                sendAgain();
            }
        });
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
