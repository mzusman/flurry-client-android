package com.mzusman.bluetooth.fragments;

import android.app.Activity;
import android.content.Context;
import android.location.LocationListener;
import android.util.JsonWriter;
import android.util.Log;
import android.widget.ListView;

import com.mzusman.bluetooth.model.GPSManager;
import com.mzusman.bluetooth.model.Model;
import com.mzusman.bluetooth.model.NetworkManager;
import com.mzusman.bluetooth.utils.Constants;
import com.mzusman.bluetooth.utils.DetailsAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/*
 * Class : DetailsTask.
 * Created by mzusman - morzusman@gmail.com on 4/19/16.
 */
public class DetailsTask extends Thread {


    ListView listView;
    DetailsAdapter detailsAdapter;
    Activity activity;
    ArrayList<String> readings;
    JsonWriter jsonWriter;
    FileOutputStream fileOutputStream;
    LocationListener locationListener;

    /**
     * ListView in order to post it with the main loop
     */
    public DetailsTask(LocationListener locationListener, Activity activity, ListView listView) {
        this.listView = listView;
        this.activity = activity;
        this.detailsAdapter = (DetailsAdapter) listView.getAdapter();
        this.locationListener = locationListener;
    }

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
                Thread.sleep(300);
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

    private synchronized void initJsonWriting() throws IOException {
        fileOutputStream = activity.openFileOutput("data2.json", Context.MODE_PRIVATE);
        jsonWriter = new JsonWriter(new OutputStreamWriter(fileOutputStream, "UTF-8"));
        jsonWriter.beginArray();
    }

    private synchronized void writeToJson(ArrayList<String> readings) throws IOException {
        jsonWriter.beginObject();
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

    private synchronized void endJsonWrite() throws IOException {
        jsonWriter.endArray();
        jsonWriter.close();
        this.fileOutputStream.close();
    }

    public void stopRunning() {
        this.interrupt();
        try {
            this.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
