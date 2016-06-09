package com.mzusman.bluetooth.model;

import android.app.Application;
import android.content.Context;

/**
 * Created by mzeus on 09/06/16.
 */
public class FlurryApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        FlurryApplication.context = getApplicationContext();
    }

    public static Context getContext() {
        return FlurryApplication.context;
    }
}
