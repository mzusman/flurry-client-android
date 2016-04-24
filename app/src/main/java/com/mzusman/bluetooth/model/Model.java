package com.mzusman.bluetooth.model;

import java.util.ArrayList;

/**
 * Created by zusmanmo on 15/04/2016.
 */
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

    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    public NetworkManager getNetworkManager(String username, String password) {
        if (networkManager == null)
            networkManager = new NetworkManager(username,password);
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
