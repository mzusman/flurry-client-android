package com.mzusman.bluetooth.model.Managers;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * Class : GpsManager.
 * Created by mzusman - morzusman@gmail.com on 4/16/16.
 *
 *
 * GPS Listener is done through the main loop - so it doesnt interrupt our OBD readings
 *
 */
public class GpsManager implements Readable, LocationListener {

    double longitude = 0;
    double latitude = 0;
    boolean status;

    @Override
    public List<String> getReadings() throws IOException {
        //not used
        return null;
    }

    @Override
    public String getReading(String READ) {
        return READ + "," + Double.toString(latitude) + "," + Double.toString(longitude);
    }

    @Override
    public void onLocationChanged(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        status = true;
    }

    @Override
    public void onProviderDisabled(String provider) {
        status = false;
    }
}
