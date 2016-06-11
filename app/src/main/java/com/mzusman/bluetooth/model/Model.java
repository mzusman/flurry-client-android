package com.mzusman.bluetooth.model;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.JsonWriter;

import com.mzusman.bluetooth.commands.ObdCommand;
import com.mzusman.bluetooth.enums.AvailableCommandNames;
import com.mzusman.bluetooth.model.Managers.BtManager;
import com.mzusman.bluetooth.model.Managers.GpsManager;
import com.mzusman.bluetooth.model.Managers.Manager;
import com.mzusman.bluetooth.model.Managers.WifiManager;
import com.mzusman.bluetooth.model.Network.NetworkManager;
import com.mzusman.bluetooth.utils.Constants;
import com.mzusman.bluetooth.utils.logger.Log4jHelper;
import com.mzusman.bluetooth.utils.thread.DetailsThread;

import org.apache.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class Model {
    Manager manager;
    NetworkManager networkManager;
    GpsManager gpsManager;
    LocationManager locationManager;

    private JsonWriter jsonWriter;
    private FileOutputStream fileOutputStream;
    private Logger logger = Log4jHelper.getLogger("Model");
    Context context;
    HashMap<String, Manager> tagManager = new HashMap<>();


    public void setGpsManager(GpsManager gpsManager) {
        this.gpsManager = gpsManager;
    }

    private static Model instance = new Model();

    private Model() {
        context = FlurryApplication.getContext();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        tagManager.put(Constants.WIFI_TAG, new WifiManager());
        tagManager.put(Constants.BT_TAG, new BtManager());
    }

    public boolean isGpsEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static Model getInstance() {
        return instance;
    }


    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public void createNewManager(String tag) {
        if (tag == null || (manager = tagManager.get(tag)) == null)
            return;
        for (int i = 0; i < AvailableCommandNames.values().length; i++) {
            AvailableCommandNames command = AvailableCommandNames.values()[i];
            if (command.isSelected()) {
                addNewCommand(AvailableCommandNames.values()[i].getValue(), command.getCommand());
            }
        }
        return;
    }

    public Model addNewCommand(String commandName, ObdCommand obdCommand) {
        if (commandName == null || obdCommand == null)
            return this;
        manager.addCommands(commandName, obdCommand);
        return this;
    }


    public Manager getManager() {
        return this.manager;
    }

    public ArrayList<String> getReading(Activity activity) throws IOException, InterruptedException {

        if (!manager.isConnected()) {
            manager.connect(Constants.WIFI_ADDRESS);
        }
        if (gpsManager == null) {
            startGpsRequests(activity);
        }

        ArrayList<String> list = (ArrayList<String>) manager.getReadings();
        if (gpsManager != null)
            list.add(gpsManager.getReading(Constants.GPS_TAG));
        return list;
    }

    private void startGpsRequests(Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gpsManager = new GpsManager();
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.
                        checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        Constants.GPS_MIN_TIME,
                        Constants.GPS_MIN_DISTANCE,
                        gpsManager);

            }
        });

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
