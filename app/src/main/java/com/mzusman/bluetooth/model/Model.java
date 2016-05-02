package com.mzusman.bluetooth.model;

import com.mzusman.bluetooth.model.Managers.GpsManager;
import com.mzusman.bluetooth.model.Managers.Network.NetworkManager;

import java.util.ArrayList;

public class Model {
    Manager manager;
    NetworkManager networkManager;
    GpsManager gpsManager;

    public void setGpsManager(GpsManager gpsManager) {
        this.gpsManager = gpsManager;
    }

    private static Model instance = new Model();

    public static Model getInstance() {
        return instance;
    }

    public void setManager(Manager manager, String deviceAddress) {
        this.manager = manager;
    }

    public Manager getManager() {

        return this.manager;
    }


//    int i = 0;
    public ArrayList<String> getReading() {
//        ArrayList<String > strings = new ArrayList<>();
//        double time = System.currentTimeMillis();
//        for (int i = 0; i < 3; i++) {
//            strings.add(i+","+time+","+i);
//        }
//
//        return strings;
        return manager.getReadings();


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


}
