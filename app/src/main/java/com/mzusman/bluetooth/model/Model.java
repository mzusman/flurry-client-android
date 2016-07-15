package com.mzusman.bluetooth.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
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

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Callback;

public class Model {
    private Manager manager;
    private NetworkManager networkManager;
    private GpsManager gpsManager;
    private LocationManager locationManager;
    private ModelSql sql = new ModelSql();
    private Logger log = Log4jHelper.getLogger("model");
    private JsonWriter jsonWriter;
    private FileOutputStream fileOutputStream;
    private Context context;
    private HashMap<String, Manager> tagManager = new HashMap<>();
    private RideDescription currentRide;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private int id;

    public interface OnEvent {
        void onSuccess();

        void onFailure();
    }


    public int getId() {
        return id;
    }

    public void setDriverId(int id) {
        this.id = id;
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

    public void createNewManager(String tag) {
        if (tag == null || (manager = tagManager.get(tag)) == null)
            return;
        for (int i = 0; i < AvailableCommandNames.values().length; i++) {
            AvailableCommandNames command = AvailableCommandNames.values()[i];
            if (command.isSelected()) {
                addNewCommand(AvailableCommandNames.values()[i].getValue(), command.getCommand());
            }
        }
    }

    public Model addNewCommand(String commandName, ObdCommand obdCommand) {
        if (commandName == null || obdCommand == null)
            return this;
        manager.addCommands(commandName, obdCommand);
        return this;
    }


    public ArrayList<String> getReading(Activity activity) throws IOException, InterruptedException {

        if (!manager.isConnected()) {
            manager.connect(null);
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
            networkManager = new NetworkManager(getSeverIp());
        return networkManager;
    }

    public String getSeverIp() {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SERVER_IP, 0);
        return preferences.getString(Constants.SERVER_IP, Constants.DEFAULT_SERVER_IP);
    }

    public void setServerIp(String ip) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SERVER_IP, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.SERVER_IP, ip);
        editor.apply();
    }

    private static final String USER_PREF = "USER";

    public void onLogin(String username, String password, Callback<NetworkManager.UserCredentials> callback) {
        setNetworkManager(username, password).loginUser(callback);
        SharedPreferences preferences = context.getSharedPreferences(USER_PREF, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_PREF, username + "," + password);
        editor.apply();
    }


    public String getLogin() {
        SharedPreferences preferences = context.getSharedPreferences(USER_PREF, 0);
        return preferences.getString(USER_PREF, null);
    }

    public String getDeviceAddress(String tag) {
        SharedPreferences preferences = context.getSharedPreferences(tag, 0);
        if (tag.equals(Constants.WIFI_TAG))
            return preferences.getString(tag, Constants.WIFI_ADDRESS);
        else return preferences.getString(tag, "0");
    }


    public void setDeviceAddress(String address, String tag) {
        SharedPreferences preferences = context.getSharedPreferences(tag, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(tag, address);
        editor.apply();
    }

    public NetworkManager setNetworkManager(String username, String password) {
        if (networkManager == null)
            networkManager = new NetworkManager(getSeverIp(), username, password);
        return networkManager;
    }


    public void setNetworkManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
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
            String currentTime = sdf.format(new Date());
            if (currentRide != null) {
                this.addRideToDatabase(currentRide);
                currentRide = null;
            }
            currentRide = new RideDescription(false, currentTime, String.valueOf(id));
            fileOutputStream = context.openFileOutput(currentRide.getFileName(), Context.MODE_PRIVATE);
            jsonWriter = new JsonWriter(new OutputStreamWriter(fileOutputStream, "UTF-8"));
            jsonWriter.beginArray();

        } catch (IOException e) {
            log.debug(e.getMessage());
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
            log.debug(e.getMessage());
        }
    }

    /**
     * this method is crucial . it writes down the json and ends the json array
     */
    public void endJsonWrite() {
        try {
            jsonWriter.endArray();
            jsonWriter.close();
            fileOutputStream.close();
            jsonWriter = null;
            fileOutputStream = null;
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
    }

    public DateFormat getDateFormat() {
        return sdf;
    }

    public List<RideDescription> getAllDriverRides() {
        return sql.getAllDriverRides(String.valueOf(id));
    }

    public void addRideToDatabase(RideDescription description) {
        sql.add(description);
    }


    private String loadFromFile(String fileName) throws IOException {
        File file = context.getFileStreamPath(fileName);
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fileInputStream.read(data);
        fileInputStream.close();
        return new String(data, "UTF-8");
    }

    public void sendRemote(final RideDescription rideDescription, final OnEvent event) {
        String data = null;
        try {
            data = loadFromFile(rideDescription.getFileName());
            log.info("load from file " + rideDescription.getFileName() + " success");
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
        Model.getInstance().getNetworkManager().sendData(Integer.parseInt(rideDescription.getDriverID()), data, new OnEvent() {
            @Override
            public void onSuccess() {
                log.debug("send data success");
                rideDescription.setSent(true);
                addRideToDatabase(rideDescription);
                event.onSuccess();
            }

            @Override
            public void onFailure() {
                log.debug("send data fail");
                rideDescription.setSent(false);
                addRideToDatabase(rideDescription);
                event.onFailure();
            }
        });
    }

    public void sendRemote(OnEvent event) {
        if (currentRide != null)
            sendRemote(currentRide, event);
    }


}
