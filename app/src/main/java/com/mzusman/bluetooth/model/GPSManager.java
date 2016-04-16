package com.mzusman.bluetooth.model;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import java.util.List;

/*
 * Class : GPSManager.
 * Created by mzusman - morzusman@gmail.com on 4/16/16.
 */
public class GPSManager implements Manager ,LocationListener{

	double longitude = 0;
	double latitude= 0;


	@Override public void connect(String deviceAddress) {
	}

	@Override public List<String> getReadings() {
		return null;
	}

	@Override public void stop() {
	}

	@Override public String getReading(String READ) {
		return READ +","+Double.toString(latitude)+","+Double.toString(longitude);
	}

	@Override public void onLocationChanged(Location location) {
		longitude = location.getLongitude();
		latitude = location.getLatitude();
	}

	@Override public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override public void onProviderEnabled(String provider) {

	}

	@Override public void onProviderDisabled(String provider) {

	}
}
