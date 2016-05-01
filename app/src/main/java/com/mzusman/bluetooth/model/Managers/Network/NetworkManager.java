package com.mzusman.bluetooth.model.Managers.Network;

import android.util.Base64;
import android.util.Log;

import com.mzusman.bluetooth.utils.Constants;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
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
    private static Retrofit.Builder builder = new Retrofit.Builder().
            addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://54.152.123.228/api/v1/flurry/");

    //non auth constructor
    public NetworkManager() {
        retrofit = builder.build();
        if (driverService == null)
            driverService = retrofit.create(DriverService.class);
        this.username = null;
        this.password = null;
    }


    public NetworkManager(String username, String password) {
//        connect(username, password);
        makeAuthorizationHeader(username, password);
        this.username = username;
        this.password = password;
    }


//    private void connect(String username, String password) {
//
//
//    }

    private void makeAuthorizationHeader(String username, String password) {
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
        OkHttpClient okHttpClient = builder.build();
        retrofit = this.builder.client(okHttpClient).build();
        if (driverService == null)
            driverService = retrofit.create(DriverService.class);


    }

    public void sendData(int driverID, String drivingData, Callback<Void> callback) {
        this.driverID = driverID;
        Call<Void> call = driverService.createDrivingData(driverID, drivingData);
        call.enqueue(callback);
    }

    public void regsiterUser(String username, String driverName, String password, Callback<UserCreditials> callback) throws IOException {
        UserRegister user = new UserRegister(username, driverName, password);
        Call<UserCreditials> call = driverService.registerDriver(user);
        call.enqueue(callback);

        this.username = username;
        this.password = password;
        makeAuthorizationHeader(username, password);


    }

    public void loginUser(Callback<NetworkManager.UserCreditials> callback) {

        Call<UserCreditials> call = driverService.loginDriver();
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

    public class UserLogin {
        public String username;
        public String password;

        public UserLogin(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    public class UserCreditials {
        public int driver_id;
        public int user_id;

        public UserCreditials(int driver_id, int user_id) {
            this.driver_id = driver_id;
            this.user_id = user_id;
        }
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDriverID(int driverID) {
        this.driverID = driverID;
    }
}
