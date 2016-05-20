package com.mzusman.bluetooth.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonWriter;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.mzusman.bluetooth.model.Managers.GpsManager;
import com.mzusman.bluetooth.model.Model;
import com.mzusman.bluetooth.utils.adapters.DetailsAdapter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

/*
 * Class : DetailsThread.
 * Created by mzusman - morzusman@gmail.com on 4/19/16.
 */

/**
 * Extension to thread class - runnable that updates the fragment screen and update the data .
 */
public class DetailsThread extends Thread {


    Callback callback;
    final DetailsThread.Event event;
    private final static int SLEEP_TIME = 500;
    private ListView listView;
    private DetailsAdapter detailsAdapter;
    private Activity activity;
    private ArrayList<String> readings;
    private JsonWriter jsonWriter;
    private FileOutputStream fileOutputStream;
    private LocationListener locationListener;
    private SpotsDialog spotsDialog;
    private TextView textView;
    private boolean run = true;
    private long time = 0;

    /**
     * ListView in order to post it with the main loop
     */
    public DetailsThread(@NonNull LocationListener locationListener, @NonNull Activity activity, @NonNull ListView listView, TextView timeView) {
        this.listView = listView;
        this.activity = activity;
        this.detailsAdapter = (DetailsAdapter) listView.getAdapter();
        this.locationListener = locationListener;
        this.textView = timeView;
        ((AppCompatActivity) activity).getSupportActionBar().setTitle("Details");
        showDialog("Loading...");
        event = new Event();
    }

    private void showDialog(String message) {
        this.spotsDialog = new SpotsDialog(activity, message);
        this.spotsDialog.setCancelable(false);
        this.spotsDialog.show();
    }

    private synchronized void disposeDialog() {
        if (spotsDialog == null)
            return;
        if (spotsDialog.isShowing()) {
            spotsDialog.dismiss();
            spotsDialog = null;
        }
    }

    class Event {
        void onEvent() {
            tryConnectToObd();
        }
    }

    /**
     * updates the activity's tool bar and the list view with strings that are given him from the
     * managers inside the model component
     */
    public void run() {
        initJsonWriting();//initiates the json reading
        tryConnectToObd();
        disposeDialog();
        try {
            sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (run) {
            try {
                time = System.currentTimeMillis();
                readings = Model.getInstance().getReading();//assigned to the Manager .
            } catch (IOException e) {
                tryConnectToObd();
                disposeDialog();
            }
            readings.add(((GpsManager) locationListener).getReading(Constants.GPS_TAG));
            writeToJson(readings);
            //run on the main thread to update the listview
            listView.post(new Runnable() {
                @Override
                public void run() {
                    detailsAdapter.setArray(readings);
                    detailsAdapter.notifyDataSetChanged();
                    textView.setText("time: " + Long.toString(time));
                }
            });
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                Log.d(Constants.RUN_TAG, "run: interrupted while sleeping");
            }
        }
        endJsonWrite();
    }

    void tryConnectToObd() {
        try {
            Model.getInstance().getManager().connect(Constants.WIFI_ADDRESS);
        } catch (IOException e) {
            disposeDialog();
            tryAgainDialog();
            synchronized (event) {
                try {
                    event.wait();
                } catch (InterruptedException e1) {//ignored
                    e1.printStackTrace();
                }
                event.onEvent();
            }
        } catch (InterruptedException e) {
            disposeDialog();
            tryAgainDialog();
        }
    }

    void tryAgainDialog() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        synchronized (event) {
                            event.notify();
                        }
                        showDialog("Connecting..");
                    }
                }).setCancelable(false).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        run = false;
                        activity.finish();
                    }
                }).setMessage("Unable to connect to the OBD device, Click 'Ok' to try again").show();
            }
        });


    }


    /**
     * three methods that are writing the data into a json
     */
    private void initJsonWriting() {
        try {
            fileOutputStream = activity.openFileOutput(Constants.FILE_DATA, Context.MODE_PRIVATE);
            jsonWriter = new JsonWriter(new OutputStreamWriter(fileOutputStream, "UTF-8"));
            jsonWriter.beginArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * writing to Json - hard coded to time-speed-rpm-gps
     */

    private void writeToJson(ArrayList<String> readings) {
        try {
            jsonWriter.beginObject();
            jsonWriter.name("time").value(time);
            /**
             * through all of the reading and write them to the json ,
             * all of the parameters are splitted by ',' -> name,time,value.
             */
            String[] reads;
            for (String read : readings) {
                reads = read.split(",");
                jsonWriter.name(reads[0]).value(reads[1]);
            }
            /**
             * gps reading - separated with latitude and longitude
             */
            jsonWriter.name(Constants.GPS_TAG);
            jsonWriter.beginObject();
            String[] strings = readings.get(readings.size() - 1).split(",");
            jsonWriter.name("lat").value(strings[1]);
            jsonWriter.name("lon").value(strings[2]);
            jsonWriter.endObject();
            jsonWriter.endObject();
            jsonWriter.flush();
        } catch (IOException ignored) {
        }
    }

    /**
     * this method is crucial . it writes down the json and ends the json array
     */
    private void endJsonWrite() {
        try {
            jsonWriter.endArray();
            jsonWriter.close();
            this.fileOutputStream.close();
        } catch (IOException e) {
            Log.d(Constants.IO_TAG, "endJsonWrite: cannot close");
//            errorEscape("failed to close the json");
        }
    }

    /*
    waits till the thread will stop - shows loading dialog meanwhile
     */
    public void stopRunning(Callback callback) {
        this.callback = callback;
        showDialog("Stopping...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DetailsThread.this.run = false;
                    if (DetailsThread.this.isAlive())
                        DetailsThread.this.join();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DetailsThread.this.disposeDialog();
                            DetailsThread.this.callback.ThreadDidStop();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public interface Callback {
        void ThreadDidStop();

    }


}
