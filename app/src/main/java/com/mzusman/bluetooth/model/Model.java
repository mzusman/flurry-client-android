package com.mzusman.bluetooth.model;

import java.util.ArrayList;

public class Model {
    Manager manager;
    NetworkManager networkManager;
    GPSManager gpsManager;


    public void setGpsManager(GPSManager gpsManager) {
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


    public ArrayList<String> getReading() {
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
    public GPSManager getGpsManager() {
        return gpsManager;
    }

    public String getRead(String READINGS) {
        return manager.getReading(READINGS);
    }

    public void drop() {
        manager.stop();
    }


}
