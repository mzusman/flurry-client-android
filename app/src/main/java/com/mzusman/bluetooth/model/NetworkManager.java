package com.mzusman.bluetooth.model;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

import com.mzusman.bluetooth.R;
import com.mzusman.bluetooth.fragments.FragmentProfile;
import com.mzusman.bluetooth.utils.Constants;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
 * Class : NetworkManager.
 * Created by mzusman - morzusman@gmail.com on 4/19/16.
 */
public class NetworkManager {

    int driverID  ;
    DriverService driverService;

    public NetworkManager() {
        connect();
    }

    public void connect() {
        Retrofit retrofit = new Retrofit.Builder().
                addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://54.152.123.228/api/v1/flurry/").build();
        driverService = retrofit.create(DriverService.class);
    }

    public void sendData(int driverID, String drivingData,Callback<Void> callback) {

        this.driverID = driverID;
        Log.d(Constants.IO_TAG, driverID + drivingData);
        Call<Void> call = driverService.createDrivingData(driverID, drivingData);
        Log.d(Constants.IO_TAG, "sendData:" + call.request().toString());
        call.enqueue(callback);
    }

    public void getDriverID(String username, String driverName, Callback<String> callback) throws IOException {
        User user = new User(username, driverName);
        Call<String> call = driverService.registerDriver(user);
        Log.d(Constants.IO_TAG, "getDriverID: " + call.request().toString() + " " + call.request().body().toString());
        call.enqueue(callback);


    }

    class User {
        String username;
        String name;

        public User(String username, String driverName) {
            this.username = username;
            this.name = driverName;
        }
    }

}
