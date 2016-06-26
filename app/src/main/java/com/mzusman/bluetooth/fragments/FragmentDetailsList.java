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
import android.net.ConnectivityManager;
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
import java.lang.reflect.Method;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentDetailsList extends Fragment {


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
        String manager = getArguments().getString(Constants.MANAGER_TAG);
        String address = getArguments().getString(Constants.DEVICE_TAG);
        Model.getInstance().setDeviceAddress(address);
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

    interface CallbackWait {
        void onWaitStop();
    }

    private void waitForNSeconds(final int n, final CallbackWait callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = n; i >= 0; i--) {
                    final int finalI = i;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            changeDialogMessage(String.format("Waiting(%d)...", finalI));
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissDialog();
                        }
                    });
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onWaitStop();
                    }
                });
            }
        }).start();
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

                waitForNSeconds(7, new CallbackWait() {
                    @Override
                    public void onWaitStop() {
                        showDialog("Trying to send...");
                        Model.getInstance().sendRemote(new Model.OnEvent() {
                            @Override
                            public void onSuccess() {
                                dismissDialog();
                                onSentSuccess();
                            }

                            @Override
                            public void onFailure() {
                                dismissDialog();
                                sendAgain();
                            }
                        });
                    }
                });
            }
        });
    }

    void sendAgain() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setCancelable(false).setMessage("Cannot connect to the cloud, Please try again.")
                .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        showDialog("Sending...");
                        Model.getInstance().sendRemote(new Model.OnEvent() {
                            @Override
                            public void onSuccess() {
                                dismissDialog();
                                onSentSuccess();
                            }

                            @Override
                            public void onFailure() {
                                dismissDialog();
                                sendAgain();
                            }
                        });
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new FragmentProfile(),
                                Constants.DETAILS_TAG).commit();
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

    private void showDialog(String message) {
        dialog = new SpotsDialog(getActivity(), message);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void changeDialogMessage(String message) {
        if (dialog == null)
            showDialog(message);
        dialog.setMessage(message);

    }


    private void dismissDialog() {
        if (dialog == null)
            return;
        if (dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
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
