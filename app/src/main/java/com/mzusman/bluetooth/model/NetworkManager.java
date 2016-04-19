package com.mzusman.bluetooth.model;

import android.util.Log;

import com.mzusman.bluetooth.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/*
 * Class : NetworkManager.
 * Created by mzusman - morzusman@gmail.com on 4/19/16.
 */
public class NetworkManager {

    Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl("http://54.152.123.228/api/v1/flurry/").build();
    DriverService driverService;


    public void connect() {

        driverService = retrofit.create(DriverService.class);

    }

    public void sendData(int DriverID, String drivingData) {

        Log.d(Constants.IO_TAG, DriverID + drivingData);
        Call<String> call = driverService.createDrivingData(DriverID, drivingData);
        Log.d(Constants.IO_TAG, "sendData:"+call.request().toString());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d(Constants.IO_TAG, "onResponse: " + response.message());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }


}
