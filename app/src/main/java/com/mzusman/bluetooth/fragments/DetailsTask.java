package com.mzusman.bluetooth.fragments;

import android.app.Activity;
import android.content.Context;
import android.location.LocationListener;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonWriter;
import android.widget.ListView;

import com.mzusman.bluetooth.model.GPSManager;
import com.mzusman.bluetooth.model.Model;
import com.mzusman.bluetooth.utils.Constants;
import com.mzusman.bluetooth.utils.DetailsAdapter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

/*
 * Class : DetailsTask.
 * Created by mzusman - morzusman@gmail.com on 4/19/16.
 */

/**
 * Extension to thread class - runnable that updates the fragment screen and update the data .
 */
public class DetailsTask extends Thread {


    final static int SLEEP_TIME = 500;
    ListView listView;
    DetailsAdapter detailsAdapter;
    Activity activity;
    ArrayList<String> readings;
    JsonWriter jsonWriter;
    FileOutputStream fileOutputStream;
    LocationListener locationListener;
    SpotsDialog spotsDialog;

    /**
     * ListView in order to post it with the main loop
     */
    public DetailsTask(LocationListener locationListener, Activity activity, ListView listView) {
        this.listView = listView;
        this.activity = activity;
        this.detailsAdapter = (DetailsAdapter) listView.getAdapter();
        this.locationListener = locationListener;
        this.spotsDialog = new SpotsDialog(activity, "Loading..");
        this.spotsDialog.show();
    }

    /**
     * updates the activity's tool bar and the list view with strings that are given him from the
     * managers inside the model component
     */
    public void run() {
        try {
            initJsonWriting();//initates the json reading
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((AppCompatActivity) activity).getSupportActionBar().setTitle("Connecting to the device");
                }
            });
            /*
            DONT FORGET TO UNCOMMENT
             */
//            Model.getInstance().getManager().connect(Constants.WIFI_ADDRESS);

            //get the readings from the manager
            readings = Model.getInstance().getReading();//assigned to the Manager .
            if (spotsDialog.isShowing())
                spotsDialog.dismiss();
            // while loop until the thread is being interrupted
            GPSManager gpsManager = Model.getInstance().getGpsManager();
            while (!Thread.currentThread().isInterrupted()) {

                /*
                Readings from both GPS manager and Model's Manager
                 */
                readings = Model.getInstance().getReading();//assigned to the Manager .
//                readings.add(((GPSManager) locationListener).getReading(Constants.GPS_TAG));
                readings.add(gpsManager.getReading(Constants.GPS_TAG));

                writeToJson(readings);
                /**
                 * way to update the UI details from different thread than main loop
                 */
                listView.post(new Runnable() {
                    @Override
                    public void run() {
                        ((AppCompatActivity) activity).getSupportActionBar().setTitle("Reading...");
                        detailsAdapter.setArray(readings);
                        detailsAdapter.notifyDataSetChanged();

                    }
                });
                Thread.sleep(SLEEP_TIME);
            }

            endJsonWrite();

        } catch (IOException e) {
            e.printStackTrace();
            try {
                endJsonWrite();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            try {
                endJsonWrite();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * three methods that are writing the data into a json
     */
    private synchronized void initJsonWriting() throws IOException {
        fileOutputStream = activity.openFileOutput("data2.json", Context.MODE_PRIVATE);
        jsonWriter = new JsonWriter(new OutputStreamWriter(fileOutputStream, "UTF-8"));
        jsonWriter.beginArray();
    }

    /**
     * writing to Json - hard coded to time-speed-rpm-gps
     */

    private synchronized void writeToJson(ArrayList<String> readings) throws IOException {
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
        String[] strings = readings.get(readings.size()-1).split(",");
        jsonWriter.name("lat").value(strings[2]);
        jsonWriter.name("lon").value(strings[3]);
        jsonWriter.endObject();
        jsonWriter.endObject();
    }

    /**
     * this method is crucial . it writes down the json and ends the json array
     */
    private synchronized void endJsonWrite() throws IOException {
        jsonWriter.endArray();
        jsonWriter.close();
        this.fileOutputStream.close();
    }

    /*
    waits till the thread will stop - shows loading dialog meanwhile
     */
    public void stopRunning() {
        SpotsDialog dialog = new SpotsDialog(activity, "Stoping..");
        dialog.show();
        this.interrupt();
        try {
            this.join();
            dialog.dismiss();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
