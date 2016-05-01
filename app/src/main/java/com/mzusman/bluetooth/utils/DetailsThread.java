package com.mzusman.bluetooth.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationListener;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.JsonWriter;
import android.util.Log;
import android.widget.ListView;

import com.mzusman.bluetooth.model.Managers.GpsManager;
import com.mzusman.bluetooth.model.Model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

/*
 * Class : DetailsThread.
 * Created by mzusman - morzusman@gmail.com on 4/19/16.
 */

/**
 * Extension to thread class - runnable that updates the fragment screen and update the data .
 */
public class DetailsThread extends Thread {


    final static int SLEEP_TIME = 1000;
    private ListView listView;
    private DetailsAdapter detailsAdapter;
    private Activity activity;
    private ArrayList<String> readings;
    private JsonWriter jsonWriter;
    private FileOutputStream fileOutputStream;
    private LocationListener locationListener;
    private SpotsDialog spotsDialog;

    /**
     * ListView in order to post it with the main loop
     */
    public DetailsThread(@NonNull LocationListener locationListener, @NonNull Activity activity, @NonNull ListView listView) {
        this.listView = listView;
        this.activity = activity;
        this.detailsAdapter = (DetailsAdapter) listView.getAdapter();
        this.locationListener = locationListener;
        showDialog("Loading...");
    }

    private void showDialog(String message) {
        this.spotsDialog = new SpotsDialog(activity, message);
        this.spotsDialog.show();
    }

    private void disposeDialog() {
        if (spotsDialog == null)
            return;
        if (spotsDialog.isShowing()) {
            spotsDialog.dismiss();
            spotsDialog = null;
        }
    }

//    private void changeActivityToolBar(String message) {
//        ((AppCompatActivity) activity).getSupportActionBar().setTitle(message);
//    }

    /**
     * updates the activity's tool bar and the list view with strings that are given him from the
     * managers inside the model component
     */
    public void run() {
        try {
            Model.getInstance().getManager().connect(Constants.WIFI_ADDRESS);
        } catch (IOException e) {
            Log.d(Constants.IO_TAG, "run: IO exception - cannot connect");
            disposeDialog();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    errorEscape("cannot connect device - try again");
                }
            });
            while (true);
        }
        //get the readings from the manager
        initJsonWriting();//initiates the json reading
        disposeDialog();
        readings = Model.getInstance().getReading();//assigned to the Manager .
        // while loop until the thread is being interrupted
        while (!Thread.currentThread().isInterrupted()) {
            readings = (ArrayList<String>) readFromManagers();
            writeToJson(readings);
            //run on the main thread to update the listview
            listView.post(new Runnable() {
                @Override
                public void run() {
                    detailsAdapter.setArray(readings);
                    detailsAdapter.notifyDataSetChanged();
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


    void errorEscape(String message) {
        AlertDialog.Builder error = new AlertDialog.Builder(activity).
                setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                }).setMessage(message);
        AlertDialog alertDialog = error.create();
        alertDialog.show();
    }


    /**
     * fills an anonymos array that assigned with data from the manager(whatever it be)
     */

    private List<String> readFromManagers() {
        ArrayList<String> arrayList;
        arrayList = Model.getInstance().getReading();//assigned to the Manager .
        arrayList.add(((GpsManager) locationListener).getReading(Constants.GPS_TAG));
        return arrayList;
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
            jsonWriter.name("time").value(readings.get(0).split(",")[1]);
            /**
             * through all of the reading and write them to the json ,
             * all of the parameters are splitted by ',' -> name,time,value.
             */
            String[] reads;
            for (String read : readings) {
                reads = read.split(",");
                jsonWriter.name(reads[0]).value(reads[2]);
            }
            /**
             * gps reading - separated with latitude and longitude
             */
            jsonWriter.name(Constants.GPS_TAG);
            jsonWriter.beginObject();
            String[] strings = readings.get(readings.size() - 1).split(",");
            jsonWriter.name("lat").value(strings[2]);
            jsonWriter.name("lon").value(strings[3]);
            jsonWriter.endObject();
            jsonWriter.endObject();
        } catch (IOException e) {
            Log.d(Constants.IO_TAG, "writeToJson: failed to write");
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
        }
    }

    /*
    waits till the thread will stop - shows loading dialog meanwhile
     */
    public void stopRunning() {
        showDialog("Stopping...");
        this.interrupt();
        try {
            this.join();
            disposeDialog();
        } catch (InterruptedException e) {
        }
    }


}
