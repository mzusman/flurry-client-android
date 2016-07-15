package com.mzusman.bluetooth.model.Network;

import android.util.Base64;
import android.util.Log;

import com.mzusman.bluetooth.model.Model;
import com.mzusman.bluetooth.utils.Constants;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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

    String username;
    String password;
    int driverID;
    DriverService driverService;
    private static Retrofit retrofit;

    private Retrofit.Builder builder;

    //non auth constructor
    public NetworkManager(String ip) {
        builder = new Retrofit.Builder().
                addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://" + ip + "/api/v1/flurry/");
        retrofit = builder.build();
        if (driverService == null)
            driverService = retrofit.create(DriverService.class);
        this.username = null;
        this.password = null;
    }


    public NetworkManager(String ip, String username, String password) {
        builder = new Retrofit.Builder().
                addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://" + ip + "/api/v1/flurry/");
        makeAuthenticationHeader(username, password);
        this.username = username;
        this.password = password;
    }


    private void makeAuthenticationHeader(String username, String password) {
        String credentials = username + ":" + password;
        final String basic = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request.Builder builderRequest = original.newBuilder()
                        .header("Authorization", basic)
                        .method(original.method(), original.body());

                Request request = builderRequest.build();
                Log.d(Constants.IO_TAG, "intercept: " + request.headers().toString());
                return chain.proceed(request);
            }
        });
        builder.readTimeout(60, TimeUnit.SECONDS).connectTimeout(60, TimeUnit.SECONDS);
        OkHttpClient okHttpClient = builder.build();
        retrofit = this.builder.client(okHttpClient).build();
        driverService = retrofit.create(DriverService.class);
    }

    public void sendData(int driverID, String drivingData, final Model.OnEvent event) {
        this.driverID = driverID;
        Call<Void> call = driverService.createDrivingData(driverID, drivingData);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful())
                    event.onSuccess();
                else event.onFailure();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                event.onFailure();
            }
        });
    }

    public void registerUser(String username, String driverName, String password, Callback<UserCredentials> callback) throws IOException {
        UserRegister user = new UserRegister(username, driverName, password);
        Call<UserCredentials> call = driverService.registerDriver(user);
        call.enqueue(callback);

        this.username = username;
        this.password = password;
        makeAuthenticationHeader(username, password);
    }

    public void loginUser(Callback<UserCredentials> callback) {
        Call<UserCredentials> call = driverService.loginDriver();
        call.enqueue(callback);
    }

    class UserRegister {
        public String username;
        public String name;
        public String password;

        public UserRegister(String username, String name, String password) {
            this.username = username;
            this.name = name;
            this.password = password;
        }
    }

    public class UserCredentials {
        public int driver_id;
        public int user_id;

        public UserCredentials(int driver_id, int user_id) {
            this.driver_id = driver_id;
            this.user_id = user_id;
        }
    }

}
