package com.mzusman.bluetooth.model;

/**
 * Created by zusmanmo on 15/04/2016.
 */
public class Connect {
    BTManager btManager;
    WifiManager wifiManager;

    private static final Connect instance = new Connect();

    public static Connect getInstance() {
        return instance;
    }
    public Connect(){


    }


}
