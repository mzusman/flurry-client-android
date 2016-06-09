package com.mzusman.bluetooth.model;

import android.app.Application;
import android.content.Context;
import android.util.JsonWriter;
import android.widget.SpinnerAdapter;

import com.mzusman.bluetooth.model.Managers.GpsManager;
import com.mzusman.bluetooth.model.Managers.Network.NetworkManager;
import com.mzusman.bluetooth.utils.Constants;
import com.mzusman.bluetooth.utils.logger.Log4jHelper;

import org.apache.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Model {
    Manager manager;
    NetworkManager networkManager;
    GpsManager gpsManager;

    private JsonWriter jsonWriter;
    private FileOutputStream fileOutputStream;
    private Logger logger = Log4jHelper.getLogger("Model");
    Context context;

    public void setGpsManager(GpsManager gpsManager) {
        this.gpsManager = gpsManager;
    }

    private static Model instance = new Model();

    private Model() {
        context = FlurryApplication.getContext();
    }

    public static Model getInstance() {
        return instance;
    }

    public void setManager(Manager manager, String deviceAddress) {
        this.manager = manager;
    }

    public Manager getManager() {
        return this.manager;
    }


    public ArrayList<String> getReading() throws IOException, InterruptedException {

        if (!manager.isConnected()) {
            manager.connect(Constants.WIFI_ADDRESS);
        }
        ArrayList<String> list = manager.getReadings();
        list.add(gpsManager.getReading(Constants.GPS_TAG));
        return list;
    }


    /**
     * @return returns null if networkmanager was'nt created - use it only if networkmanager was
     * used once
     */
    public NetworkManager getNetworkManager() {
        if (networkManager == null)
            networkManager = new NetworkManager();
        return networkManager;
    }

    public NetworkManager setNetworkManager(String username, String password) {
        if (networkManager == null)
            networkManager = new NetworkManager(username, password);
        return networkManager;
    }

    public void setNetworkManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    /*
    @return - may return null if the setGpsManager was'nt invoked
     */
    public GpsManager getGpsManager() {
        return gpsManager;
    }

    public String getRead(String READINGS) {
        return manager.getReading(READINGS);
    }

    public void drop() {
        manager.stop();
    }


    public void writeToFile(ArrayList<String> arrayList, long time) {
        if (jsonWriter == null)
            initJsonWriting();
        writeToJson(arrayList, time);

    }

    /**
     * three methods that are writing the data into a json
     */
    private void initJsonWriting() {
        try {
            fileOutputStream = context.openFileOutput(Constants.FILE_DATA, Context.MODE_PRIVATE);
            jsonWriter = new JsonWriter(new OutputStreamWriter(fileOutputStream, "UTF-8"));
            jsonWriter.beginArray();

        } catch (IOException e) {
            logger.debug(e.getMessage());
        }
    }

    private void writeToJson(ArrayList<String> readings, long time) {
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
        } catch (IOException e) {
            logger.debug(e.getMessage());
        }
    }

    /**
     * this method is crucial . it writes down the json and ends the json array
     */
    public void endJsonWrite() {
        try {
            jsonWriter.endArray();
            jsonWriter.close();
            this.fileOutputStream.close();
            jsonWriter = null;
            fileOutputStream = null;
        } catch (IOException e) {
            logger.debug(e.getMessage());
        }
    }

    public void writeToLog(String message) {
        logger.debug(message);
    }

}
